import random
import utils
from customerrors import RFGError
import os
from scipy import stats

class Generator:
    def __init__(self, test_type, precentage_of_safty_clauses=50, clause_lengths=[2,3,4,6,8,10],
                    clauses_num=50, poisson=False, atoms_num_coeff=0.5, problem5_distribution="even", lambda_value=3.5):

        # create lists in which all the formulas will be stored
        self.formula = []
        self.formula2 = []
        self.formula3 = []
        self.formulaR = []

        # calculate the number of atoms to generate       
        atoms_num = round(clauses_num*atoms_num_coeff)

        # remember parameters which will be used later on
        self.test_type = test_type
        self.lambda_value = lambda_value

        # create a dictionary listing all atoms and number of their occurences in formulas (for now 0)
        self.atom_counting_dict = {}
        for atom_cnt in range(1, atoms_num+1):
            atom_name = "var" + str(atom_cnt)
            self.atom_counting_dict[atom_name] = 0

        # build the base of output file name; for example problem1 with default parameters
        # will have base of file name "problem1_c50_a25_prec50_lengths_2_3_4_6_8_10"
        file_name_builder = [test_type, "_c", str(clauses_num), "_a", str(atoms_num), "_prec", str(precentage_of_safty_clauses), "_lengths"]
        for constant in clause_lengths:
            file_name_builder.append("_" + str(constant))
        if poisson:
            file_name_builder.append("_poisson")
        elif test_type == "problem5":
            file_name_builder.append("_distribution_")
            file_name_builder.append(problem5_distribution)
        self.output_file_name = "".join(file_name_builder)

        # generate the formulas with given parameters
        self.generate(precentage_of_safty_clauses, clause_lengths, clauses_num, poisson, atoms_num_coeff, problem5_distribution)

        # perform all the procedures required after generating the formulas
        self.cleanup(precentage_of_safty_clauses)

        # save generated formulas to a .txt file
        self.saveFormulas()

    def generate(self, precentage_of_safty_clauses, clause_lengths, clauses_num, poisson, atoms_num_coeff, problem5_distribution):
        # check if the parameters don't force a different problem type than requested by user
        if self.test_type != 'problem3' and atoms_num_coeff != 0.5:
            raise RFGError(f"Generator.generate: Test of type {self.test_type} should have atoms number coefficient equal to 0,5.", self.output_file_name)

        if self.test_type not in ['problem4','problem5'] and len(clause_lengths) <= 1:
            raise RFGError(f"Generator.generate: Test of type {self.test_type} should have more than one clause length given.", self.output_file_name)

        if self.test_type != 'problem6' and precentage_of_safty_clauses != 50:
            raise RFGError(f"Generator.generate: Tests of type {self.test_type} should have precentage of safety clauses equal to 50.", self.output_file_name)

        if self.test_type in ['problem1','problem3','problem4','problem5'] and poisson:
            raise RFGError(f"Generator.generate: Tests of type {self.test_type} do not support poisson distribution.", self.output_file_name)

        # check which problem do the parameteres represent and proceed with problem generation
        if self.test_type == 'problem1':
            self.generateProblem1(clause_lengths, clauses_num)
        elif self.test_type == 'problem2':
            self.generateProblem2(clauses_num)
        elif self.test_type == 'problem3':
            # the coefficient has to be at least 1 as it limits the length of clauses
            if atoms_num_coeff <= 1:
                raise RFGError("Generator.generate: Test of type problem3 should have atoms number coefficient grater or equal to 1.", self.output_file_name)

            self.generateProblem3(clause_lengths, clauses_num, atoms_num_coeff)
        elif self.test_type == 'problem4':
            # there needs to be exactly 1 length given
            if len(clause_lengths) != 1:
                raise RFGError("Generator.generate: Test of type problem4 should have single clause length given.", self.output_file_name)

            self.generateProblem4(clause_lengths[0], clauses_num)
        elif self.test_type == 'problem5':
            # there need to be exactly 4 lengths given
            if len(clause_lengths) != 4:
                raise RFGError("Generator.generate: Test of type problem5 should have exactly four clauses lengths given.", self.output_file_name)

            self.generateProblem5(clause_lengths, clauses_num, problem5_distribution)
        elif self.test_type == 'problem6':
            self.generateProblem6(clause_lengths, clauses_num, precentage_of_safty_clauses, poisson)
        elif self.test_type.startswith("problem7"):
            if self.test_type[-1] not in ['a','b']:
                raise RFGError("Generator.generate: Unknown subtype of problem7.", self.output_file_name)
            self.generateProblem7(clause_lengths, clauses_num, poisson)
        elif self.test_type.startswith("problem8"):
            if self.test_type[-1] not in ['a','b','c']:
                raise RFGError("Generator.generate: Unknown subtype of problem8.", self.output_file_name)
            self.generateProblem8(clause_lengths, clauses_num, poisson)
        else:
            raise RFGError("Generator.generate: Unknown name of problem.", self.output_file_name)

    def translateAndSave(self):
        if self.test_type.startswith("problem7"):
            # translate all generated formulas to provers formats and save them to files
            prov9_file1 = self.translateToProver9(raw=True, file_name_appendix="_file1_")
            prov9_file2 = self.translateToProver9(raw=True, file_name_appendix="_file2_", source_formula=self.formula2)
            prov9_file3 = self.translateToProver9(raw=True, file_name_appendix="_file3_", source_formula=self.formula3)
            prov9_fileR = self.translateToProver9(raw=True, file_name_appendix="_fileR_", source_formula=self.formulaR)
            spass_file1 = self.translateToSPASS(raw=True, file_name_appendix="_file1_")
            spass_file2 = self.translateToSPASS(raw=True, file_name_appendix="_file2_", source_formula=self.formula2)
            spass_file3 = self.translateToSPASS(raw=True, file_name_appendix="_file3_", source_formula=self.formula3)
            spass_fileR = self.translateToSPASS(raw=True, file_name_appendix="_fileR_", source_formula=self.formulaR)

            # create a pattern for joining files
            if self.test_type[-1] == 'a':
                delimiter = "OR"
            elif self.test_type[-1] == 'b':
                delimiter = "AND"
            pattern = ["NOT","LP","FILE0",delimiter,"FILE1",delimiter,"FILE2","RP","OR","FILE3"]

            # join saved files into one file using created pattern (and delete the original files afterwards)
            prov9_input_file = self.joinProver9FilesWithPattern([prov9_file1,prov9_file2,prov9_file3,prov9_fileR],pattern)
            spass_input_file = self.joinSPASSFilesWithPattern([spass_file1,spass_file2,spass_file3,spass_fileR],pattern)
        elif self.test_type.startswith("problem8"):
            # translate both generated formulas to provers formats and save them to files
            prov9_file1 = self.translateToProver9(raw=True, file_name_appendix="_file1_")
            prov9_file2 = self.translateToProver9(raw=True, file_name_appendix="_file2_", source_formula=self.formula2)
            spass_file1 = self.translateToSPASS(raw=True, file_name_appendix="_file1_")
            spass_file2 = self.translateToSPASS(raw=True, file_name_appendix="_file2_", source_formula=self.formula2)

            # create a pattern for joining files
            if self.test_type[-1] == 'a':
                pattern = ["LP","NOT","FILE0","OR","NOT","FILE1","RP","AND","LP","FILE0","OR","FILE1","RP"]
            elif self.test_type[-1] == 'b':
                pattern = ["NOT","LP","NOT","FILE0","AND","NOT","FILE1","RP"]
            elif self.test_type[-1] == 'c':
                pattern = ["LP","NOT","FILE0","OR","FILE1","RP","AND","NOT","LP","NOT","FILE1","OR","FILE0","RP"]

            # join saved files into one file using created pattern (and delete the original files afterwards)
            prov9_input_file = self.joinProver9FilesWithPattern([prov9_file1,prov9_file2],pattern)
            spass_input_file = self.joinSPASSFilesWithPattern([spass_file1,spass_file2],pattern)
        else:
            # if the problem is neither of type 7 nor 8, only one formula was generated - simply translate it and save to file
            prov9_input_file = self.translateToProver9()
            spass_input_file = self.translateToSPASS()

        # return paths to saved files (and path of the output file yet to be generated) with descriptions
        return {"prover9_input": prov9_input_file, "spass_input": spass_input_file, "output": self.output_file_name}

    def generateProblem1(self, clauses_lengths, clauses_num, safety_coeff=0.5, target_formula=1):
        formula = []
        if len(clauses_lengths) > clauses_num:
            raise RFGError(
                "Generator.generateProblem1: The number of required different clauses lengths is higher than the number of clauses.", self.output_file_name)

        # calculate how many clauses of a single length to generate
        clauses_of_one_length_number = round(clauses_num/len(clauses_lengths))
        # calculate how many clauses of a single length should be safety clauses
        safety_clauses_number = round(clauses_of_one_length_number*safety_coeff)

        # iterate through all the clauses lengths and create clauses
        for current_length in clauses_lengths:
            for clause_of_current_length_idx in range(clauses_of_one_length_number):
                # check if limit of clauses not reached due to rounding of the numbers
                if len(formula) < clauses_num:
                    # if number of generated clauses of current length is smaller than
                    # number of safety clauses of a single length to generate - generate a safety clause;
                    # else - generate a liveness clause
                    if clause_of_current_length_idx < safety_clauses_number:
                        clause = self.generateSafetyClause(current_length)
                    else:
                        clause = self.generateLivenessClause(current_length)

                    # add the generated clause to the list representing the whole formula
                    formula.append(clause)

        # check if there is a shortage of clauses due to rounding of the numbers
        clauses_shortage = clauses_num - len(formula)
        # calculate how many of the missing clauses should be safety clauses
        limit = round(clauses_shortage*safety_coeff)
        # clauses_shortage is grater or equal to 0 (because of the check earlier, in the main loop)
        # and if it's 0 the loop below will simply be skipped
        for counter in range(clauses_shortage):
            random.seed()
            # if there are clauses missing, generate them with random length from lengths list,
            # keeping the ratio of safety and liveness clauses
            if counter <= limit:
                clause = self.generateSafetyClause(random.choice(clauses_lengths))
            else:
                clause = self.generateLivenessClause(random.choice(clauses_lengths))
            formula.append(clause)

        # save the generated formula into proper list, based on target_formula parameter
        if target_formula == 1:
            self.formula = formula
        elif target_formula == 2:
            self.formula2 = formula
        elif target_formula == 3:
            self.formula3 = formula

    def generateProblem2(self, clauses_num, safety_coeff=0.5, target_formula=1):
        formula = []
        clauses_lengths = []
        probabilities = []
        # get the Poisson distribution with given lambda
        poisson_dist = stats.poisson(self.lambda_value)

        # maximal length of the clause is set equal to number of clauses
        # although with high lambda values it is possible to have longer clauses (because the shortest clauses numbers will round to 0),
        # the program will most likely fail as it won't be able to generate a clause longer than the number of atoms
        # which most often equals half of the number of clauses
        for length in range(1,clauses_num+1):
            # if number of clauses of current length rounds to 0 and it is also higher than lambda
            # it means that for all following lengths the number will also round to 0 - finish generating lengths 
            if not round(poisson_dist.pmf(length)*clauses_num) and length > self.lambda_value:
                break
            # save the length and probability of it's occurance into lists
            clauses_lengths.append(length)
            probabilities.append(poisson_dist.pmf(length))
        # based on probability, calculate how many clauses will have each length
        clauses_of_each_length_numbers = [round(prob*clauses_num) for prob in probabilities]
        # calculate how many clauses of each length should be safety clauses
        safety_clauses_numbers = [round(number*safety_coeff) for number in clauses_of_each_length_numbers]

        # iterate through all the clauses lengths and create clauses
        for current_length_idx in range(len(clauses_lengths)):
            for clause_of_current_length_idx in range(clauses_of_each_length_numbers[current_length_idx]):
                # check if limit of clauses not reached due to rounding of the numbers
                if len(formula) < clauses_num:
                    # if number of generated clauses of current length is smaller than
                    # number of safety clauses for this length - generate a safety clause;
                    # else - generate a liveness clause
                    if clause_of_current_length_idx < safety_clauses_numbers[current_length_idx]:
                        clause = self.generateSafetyClause(clauses_lengths[current_length_idx])
                    else:
                        clause = self.generateLivenessClause(clauses_lengths[current_length_idx])

                    # add the generated clause to the list representing the whole formula
                    formula.append(clause)

        # check if there is a shortage of clauses due to rounding of the numbers
        clauses_shortage = clauses_num - len(formula)
        # calculate how many of the missing clauses should be safety clauses
        limit = round(clauses_shortage*safety_coeff)
        # clauses_shortage is grater or equal to 0 (because of the check earlier, in the main loop)
        # and if it's 0 the loop below will simply be skipped
        for counter in range(clauses_shortage):
            random.seed()
            # if there are clauses missing, generate them with random length from lengths list,
            # keeping the ratio of safety and liveness clauses
            if counter <= limit:
                clause = self.generateSafetyClause(random.choice(clauses_lengths))
            else:
                clause = self.generateLivenessClause(random.choice(clauses_lengths))
            formula.append(clause)

        # save the generated formula into proper list, based on target_formula parameter
        if target_formula == 1:
            self.formula = formula
        elif target_formula == 2:
            self.formula2 = formula
        elif target_formula == 3:
            self.formula3 = formula

    def generateProblem3(self, clause_length, clauses_num, atoms_num_coeff):
        # the proper number of atoms has already benn generated in the constructor;
        # only thing left to do is to remove lengths exceeding the limit from the lengths list
        new_clause_length = [length for length in clause_length if length <= atoms_num_coeff]
        if not new_clause_length:
            raise RFGError("Generator.generateProblem3: No usable lengths", self.output_file_name)
        # generate the formula as if it was of problem1, using new clauses lengths
        self.generateProblem1(new_clause_length, clauses_num)

    def generateProblem4(self, clause_length, clauses_num, safety_coeff=0.5):
        # calculate how many of the clauses should be safety clauses
        safety_clauses_number = round(clauses_num*safety_coeff)

        # generate clauses of given length
        for clause_number in range(clauses_num):
            # if number of generated clauses is smaller than number of safety clauses to generate - 
            # generate a safety clause; else - generate a liveness clause
            if clause_number < safety_clauses_number:
                clause = self.generateSafetyClause(clause_length)
            else:
                clause = self.generateLivenessClause(clause_length)

            self.formula.append(clause)

    def generateProblem5(self, clauses_lengths, clauses_num, distribution, safety_coeff=0.5):
        if clauses_num < 4:
            raise RFGError(
                "Generator.generateProblem5: The number of required different clauses lengths is higher than the number of clauses.", self.output_file_name)

        # based on given distribution type, calculate how many clauses of each length should be generated
        # for the least represented lengths the calculation was slightly modified so that formulas with 50 clauses have at least one clause of each length
        if distribution == "even":
            clauses_of_each_length_numbers = [round(clauses_num/4) for i in range(4)]
        elif distribution == "more_long":
            clauses_of_each_length_numbers = [round(clauses_num*0.01+0.001) if i == 0 else round(clauses_num*0.33) for i in range(4)]
        elif distribution == "more_short":
            clauses_of_each_length_numbers = [round(clauses_num*0.01+0.001) if i == 3 else round(clauses_num*0.33) for i in range(4)]
        else:
            raise RFGError(
                "Generator.generateProblem5: Invalid distribution. Choose from [\"even\",\"more_long\",\"more_short\"]", self.output_file_name)
        # calculate how many clauses of each length should be safety clauses
        safety_clauses_number = [round(number*safety_coeff) for number in clauses_of_each_length_numbers]
        # sort lengths list so that it matches the order of other lists
        clauses_lengths.sort()

        # iterate through all the clauses lengths and create clauses
        for current_length_idx in range(4):
            for clause_of_current_length_idx in range(clauses_of_each_length_numbers[current_length_idx]):
                # check if limit of clauses not reached due to rounding the numbers
                if len(self.formula) < clauses_num:
                    # if number of generated clauses of current length is smaller than
                    # number of safety clauses for this length - generate a safety clause;
                    # else - generate a liveness clause
                    if clause_of_current_length_idx < safety_clauses_number[current_length_idx]:
                        clause = self.generateSafetyClause(clauses_lengths[current_length_idx])
                    else:
                        clause = self.generateLivenessClause(clauses_lengths[current_length_idx])

                    # add the generated clause to the list representing the whole formula
                    self.formula.append(clause)

        # check if there is a shortage of clauses due to rounding of the numbers
        clauses_shortage = clauses_num - len(self.formula)
        # calculate how many of the missing clauses should be safety clauses
        limit = round(clauses_shortage*safety_coeff)
        # clauses_shortage is grater or equal to 0 (because of the check earlier, in the main loop)
        # and if it's 0 the loop below will simply be skipped
        for counter in range(clauses_shortage):
            random.seed()
            # if there are clauses missing, generate them with random length from lengths list,
            # keeping the ratio of safety and liveness clauses
            if counter <= limit:
                clause = self.generateSafetyClause(random.choice(clauses_lengths))
            else:
                clause = self.generateLivenessClause(random.choice(clauses_lengths))
            self.formula.append(clause)

    def generateProblem6(self, clause_length, clauses_num, safety_precentage, poisson):
        # check if the value of safety_precentage parameter is valid precentage
        if safety_precentage < 0 or safety_precentage > 100:
            raise RFGError("Generator.generateProblem6: Safety clauses precentage not in range [0;100]", self.output_file_name)

        if poisson:
            # generate formula as if it was of problem2, with adjusted safety clauses precentage
            self.generateProblem2(clauses_num, safety_coeff=safety_precentage/100)
        else:
            # generate formula as if it was of problem1, with adjusted safety clauses precentage
            self.generateProblem1(clause_length, clauses_num, safety_coeff=safety_precentage/100)

    def generateProblem7(self, clause_length, clauses_num, poisson):
        if poisson:
            # generate 3 formulas of problem2, each time saving them to a different list
            self.generateProblem2(clauses_num, target_formula=1)
            self.generateProblem2(clauses_num, target_formula=2)
            self.generateProblem2(clauses_num, target_formula=3)
        else:
            # generate 3 formulas of problem1, each time saving them to a different list
            self.generateProblem1(clause_length, clauses_num, target_formula=1)
            self.generateProblem1(clause_length, clauses_num, target_formula=2)
            self.generateProblem1(clause_length, clauses_num, target_formula=3)
        # generate additional formula of type R
        self.generateFormulaR()

    def generateProblem8(self, clause_length, clauses_num, poisson):
        if poisson:
            # generate 2 formulas of problem2, each time saving them to a different list
            self.generateProblem2(clauses_num, target_formula=1)
            self.generateProblem2(clauses_num, target_formula=2)
        else:
            # generate 2 formulas of problem1, each time saving them to a different list
            self.generateProblem1(clause_length, clauses_num, target_formula=1)
            self.generateProblem1(clause_length, clauses_num, target_formula=2)

    def generateFormulaR(self):
        # the easiest way to produce Liveness clause in general form with more than one atom
        # is to generate it as safety clause and replace the quantifier
        self.formulaR = []
        self.formulaR.append(self.generateSafetyClause(4))
        self.formulaR[0][0] = utils.LogicToken("EXISTS")

    def generateSafetyClause(self, clause_length):
        # generate a generic clause and put a FORALL token at the beginning of it
        clause = self.generateClause(clause_length)
        clause.insert(0, utils.LogicToken("FORALL"))
        return clause

    def generateLivenessClause(self, clause_length):
        # generate a generic clause
        clause = self.generateClause(clause_length)

        # if clause has only one atom, use the general form: EXISTS t>=t0: Q(t)
        # if it's longer, use the conditional form: FORALL t>=t0 EXISTS t1<=t: P(t1) -> Q(t)
        if len(clause) > 1:
            # randomly change one OR into IMP to separate P and Q subclauses
            self.replaceORwithIMP(clause)

        # put an EXISTS token at the beginning of the clause
        clause.insert(0, utils.LogicToken("EXISTS"))
        return clause

    def generateClause(self, length):
        # randomly pick atoms to be used in the clause
        atoms = self.getRandomAtomList(length)
        relations = []

        # randomly pick relations to be inserted between the atoms
        for _ in range(length-1):
            relations.append(self.getRandomRelation())

        # zip the list of atoms with the list of relations
        clause = utils.zipToList(atoms, relations)
        return clause

    def getRandomAtomList(self, length):
        random.seed()
        atoms = []
        # get the list of all atoms
        avaliable_atoms = list(self.atom_counting_dict.keys())
        for _ in range(length):
            # if there are no more avaliable atoms, but they are still demanded, raise an error
            if len(avaliable_atoms) == 0:
                raise RFGError(
                    "Generator.getRandomAtomList: The number of atoms is smaller than length of clause.", self.output_file_name)
            # choose randomly an atom from the list
            value = random.choice(avaliable_atoms)
            # then remove it from the list so it doesn't get chosen more than once
            avaliable_atoms.remove(value)
            # increase it's counter in dictionary
            self.atom_counting_dict[value] += 1
            # decide randomly whether to negate the atom
            negation = bool(random.randint(0, 1))
            # create the atom token
            atoms.append(utils.LogicToken('ATOM', value, negation))
        # return the atoms as token list
        return atoms

    def getRandomRelation(self):
        random.seed()
        # choose randomly a relation from list of all possible ones and return it as token
        # *In this program version the only available relation type is OR*
        token = random.choice(utils.LogicToken.relation_types)
        return utils.LogicToken(token)

    def replaceORwithIMP(self, clause):
        # randomly change one OR into IMP to separate P and Q subclauses:
        random.seed()
        # pick a token randomly
        or_to_change = random.choice(clause)
        # while it's not an OR token, pick another one
        # (the chances of picking an OR token are only a little less than 50% so this method is not as inefficient as it looks)
        while(or_to_change.TokenType != "OR"):
            or_to_change = random.choice(clause)
        # change the token's type to IMP (all of it's other fields are, and should stay, empty)
        or_to_change.TokenType = "IMP"

    def cleanup(self, precentage_of_safty_clauses):
        # set a list of formulas to cleanup
        if self.test_type.startswith('problem7'):
            list_of_formulas = [self.formula, self.formula2, self.formula3]
        elif self.test_type.startswith('problem8'):
            list_of_formulas = [self.formula, self.formula2]
        else:
            list_of_formulas = [self.formula]

        # PART1: check if there are atoms which do not occur in any of the formulas
        for (key, value) in self.atom_counting_dict.items():
            if value == 0:
                # find the most frequent atom
                borrow_from = self.getMostFrequentKey()
                # if a break signal is returned, borrowing should not be performed at all
                if borrow_from == "break_signal":
                    break

                end_flag = False
                # find first occurence of most frequent atom and replace it's name, keeping the negation value
                for current_formula in list_of_formulas:
                    for clause in current_formula:
                        for token in clause:
                            if (token.TokenType == "ATOM" and token.Value == borrow_from):
                                token.Value = key
                                end_flag = True
                                break
                        if end_flag:
                            break
                    if end_flag:
                            break

                # increase the counter of the atom with zero occurences and decrease the counter of the most frequent atom
                self.atom_counting_dict[key] += 1
                self.atom_counting_dict[borrow_from] -= 1

        # PART2: check if last length of clauses has any liveness clauses
        for current_formula in list_of_formulas:
            # safety clauses occur before liveness clauses so 
            # if last clause is a safety clause, the last length of clauses has no liveness clauses
            if current_formula[-1][0].TokenType == "FORALL":
                # Change appropiate procentage of last safety clauses into liveness clauses;
                # This fixes an error where due to rounding of numbers, the last length of clauses
                # would have less clauses than other ones and not enough liveness clauses would be generated
                safety_counter = 1
                index = -2
                # count the clauses of last length
                while current_formula[index][0].TokenType == "FORALL":
                    safety_counter += 1
                    index -= 1
                # calculate the precentage of liveness clauses in the formula
                liveness_coeff = 1 - (precentage_of_safty_clauses/100)
                # replace appropriate precentage of last length clauses with liveness clauses
                for i in range(-1, -1*round(safety_counter*liveness_coeff)-1, -1):
                    # replace the quantifier token
                    current_formula[i][0].TokenType = "EXISTS"
                    # if clause has only one atom, use the general form: EXISTS t>t0: Q(t)
                    if len(current_formula[i]) > 2:
                        # else randomly change one OR into IMP to separate P and Q subclauses
                        self.replaceORwithIMP(current_formula[i])

    def getMostFrequentKey(self):
        # go through the dictionary and find the most frequent atom
        highest_frequency = 0
        most_frequent_key = None
        for (key, value) in self.atom_counting_dict.items():
            if value > highest_frequency:
                highest_frequency = value
                most_frequent_key = key

        # if the most frequent atom occures once or not at all, it means that the number
        # of atoms is higher than the cummulative length of clauses
        if (highest_frequency == 0 or highest_frequency == 1 or most_frequent_key == None):
            # which should just be noted if the problem type is problem3...
            if self.test_type == "problem3":
                return "break_signal"
            # ...but should cause an error otherwise
            else:
                raise RFGError(
                    "Generator.getMostFrequentKey: The number of atoms is to big to use all of them.", self.output_file_name)

        return most_frequent_key

    def translateToProver9(self, raw=False, file_name_appendix="_", source_formula=[]):
        # by default the source formula is the first one
        if not source_formula:
            source_formula = self.formula

        clause_str_list = []
        for clause in source_formula:
            # clear all temporary variable values
            quantifiers_list = []   # quantifiers tokens
            left_list = []          # atoms on the left side of IMP operator
            right_list = []         # atoms on the right side of IMP operator
            imp_exists = False      # a flag stating whether there is an IMP token in current clause

            for token in clause:
                if (token.TokenType in ['FORALL', 'EXISTS']):
                    # translate the quantifier into Prover9 format
                    operator = 'all' if token.TokenType == 'FORALL' else 'exists'
                    # place it in quantifiers list along with the quantified variable
                    quantifiers_list.extend([operator, 'u'])
                elif (token.TokenType == 'OR'):
                    # translate the operator into Prover9 format
                    # place it in left list if no implication was encountered or in right list otherwise
                    if imp_exists:
                        right_list.append('|')
                    else:
                        left_list.append('|')
                elif (token.TokenType == 'ATOM'):
                    # translate the atom into Prover9 format unary predicate
                    # by changing the first letter of it's name to uppercase and adding a variable as parameter
                    if str(token)[0] == 'v':
                        token_str = f'V{str(token)[1:]}(u)'
                    else:
                        token_str = f'{str(token)[0]}V{str(token)[2:]}(u)'

                    # place it in left list if no implication was encountered or in right list otherwise
                    if imp_exists:
                        right_list.append(token_str)
                    else:
                        left_list.append(token_str)
                elif (token.TokenType == 'IMP'):
                    # add the existential quantifier along with new quantified variable to quantifiers list
                    quantifiers_list.extend(['exists', 'u1'])
                    # set the implication existance flag
                    imp_exists = True
                    # this only occurs in liveness clauses
                    # so the first quantifier in the list had to be 'exists' - replace it with 'all'
                    quantifiers_list[0] = 'all'
                
            # begin the clause string by joining the quantifiers list
            clause_str = f'{" ".join(quantifiers_list)}'
            # normally the clauses end with a dot, but if the format is raw they should not
            dot = "" if raw else "."
            if imp_exists:
                # until now all predicates had variable 'u' as parameter
                # replace it with 'u1' in the left side subclause
                left_list = [elem.replace('(u)', '(u1)') for elem in left_list]
                
                # add the variables relation string and implication between left subclause and right subclause to the clause string
                clause_str += f' ((u >= u1) & (({" ".join(left_list)}) -> ({" ".join(right_list)}))){dot}'
            else:
                if quantifiers_list[0] == 'all':
                    # if clause is a safety clause, all the tokens are in left list
                    # add them to the clause string, negated
                    clause_str += f' (-({" ".join(left_list)})){dot}'
                else:
                    # the clause is a liveness clause in general form
                    # add it to the clause string
                    clause_str += f' ({" ".join(left_list)}){dot}'
            
            # add the clause string to the list of clauses strings
            clause_str_list.append(clause_str)

        # build file name and path
        script_path = os.path.dirname(__file__)
        os_sep = os.sep
        file_name = f'{self.output_file_name}{file_name_appendix}prover9.in'

        # the file is saved in a folder called "generated_files", located in the same folder as this script
        dir_path = f'{script_path}{os_sep}generated_files'
        if not os.path.exists(dir_path):
            os.mkdir(dir_path)

        path = f'{dir_path}{os_sep}{file_name}'

        # save clauses to file
        with open(path, "w+") as file:
            if not raw:
                # add memory limit
                file.write('assign(max_megs, 1024).\n\n')
                # add header
                file.write('formulas(sos).\n\n')
                # write clauses in Clause Normal Form
                file.write('\n'.join(clause_str_list))
                # add footer
                file.write('\n\nend_of_list.')
            else:
                # file format is raw: write clauses in Conjunctive Normal Form and nothing else
                file.write("((" + ") & (".join(clause_str_list) + "))")  

        return path

    def translateToSPASS(self, raw=False, file_name_appendix="_", source_formula=[]):
        # by default the source formula is the first one
        if not source_formula:
            source_formula = self.formula

        # prepare the list of descriptions
        descriptions = [
            'list_of_descriptions.',
            'name({*' + self.output_file_name + '*}).',
            'author({*Jakub Semczyszyn*}).',
            'status(unknown).',
            'description({*The problem was generated randomly*}).',
            'end_of_list.'
        ]

        clause_str_list = []
        formula_info = {
            'all_atoms': [],        # list of all atoms present in the formula
            'imp_exists': False     # a flag stating whether there is an IMP token in the formula
        }
        for clause in source_formula:
            queue = []              # a queue of tuples (TokenType, list of tokens) to be transformed into strings
            atoms = []              # a list of all atoms in the (sub)clause
            or_atoms = None         # a list of atoms in the (sub)clause joined with disjunction tokens
            first_tuple = None      # a tuple for storing the quantifier
            imp_tuple = None        # a tuple for storing the implication operator

            # generate tokens queue
            for token in clause:
                if token.TokenType in ['FORALL', 'EXISTS']:
                    # save the quantifier in the quantifier tuple
                    # the token type represents the whole quantifier, no need to save additional information
                    first_tuple = (token.TokenType, [])
                elif token.TokenType == 'OR':
                    # all atoms in atoms list will from now on appear in disjunct atoms list as well
                    if not or_atoms:
                        or_atoms = atoms
                elif token.TokenType == 'ATOM':
                    # add the atom name to the list of all atoms
                    formula_info['all_atoms'].append(token.Value)
                    # add the token to the atoms list
                    atoms.append(token)
                elif token.TokenType == 'IMP':
                    # save the implication operator in the implication tuple
                    # the token type represents the whole operator, no need to save additional information
                    imp_tuple = (token.TokenType, [])
                    # set the implication flag for the whole formula
                    formula_info['imp_exists'] = True

                    # if the left-side subclause consisted of disjunction
                    if or_atoms:
                        # create a tuple consisting of OR TokenType and list of disjunct atoms
                        or_tuple = ('OR', or_atoms)
                        # add the created tuple to the queue
                        queue.append(or_tuple)
                    # else if left-side subclause consisted of a single atom
                    elif atoms:
                        # add a tuple consisting of ATOM TokenType and list with said atom token to the queue
                        queue.append(('ATOM', atoms))

                    # left subclause has ended - clear the atoms lists and move on to the right subclause
                    atoms, or_atoms = [], None

            # if there was no implication, no atoms have been saved to the queue yet
            # even if there was an implication, the atoms of the right subclause have not been saved to the queue yet

            # if the remaining atoms were disjunct
            if or_atoms:
                # add a tuple consisting of OR TokenType and list of disjunct atoms to the queue
                queue.append(('OR', atoms))
            # else if there is only one remaining atom
            elif atoms:
                # add a tuple consisting of ATOM TokenType and list with said atom token to the queue
                queue.append(('ATOM', atoms))

            # by default the quantified variable is 'T'           
            var_str = '(T)'
            if imp_tuple:
                # if there was an implication in current clause, 'T1' variable will be used first, for the left subclause
                var_str = '(T1)'
                # add the implication tuple to the queue
                queue.append(imp_tuple)
            # add the quantifier tuple to the queue
            queue.append(first_tuple)

            # process the queue
            clause_str = ''
            for token_type, tokens in queue:
                if token_type == 'ATOM':
                    # the list consists of only one atom token
                    token = tokens[0]
                    # translate the token into SPASS format string with current variable
                    token_str = self.__getAtomStr(token, var_str)
                    # add the translation to the clause string
                    clause_str = f'{clause_str}, {token_str}' if len(clause_str) else token_str
                    # the entire (sub)clause was processed - switch to the other variable
                    var_str = '(T)' if var_str == "(T1)" else "(T1)"
                elif token_type == 'OR':
                    # the list consists of multiple atom tokens; 
                    # translate them all into SPASS format string with current variable
                    atom_str_list = []
                    for token in tokens:
                        atom_str_list.append(self.__getAtomStr(token, var_str))

                    # join all the atoms strings with commas and encapsulate them in 'or()' opearator
                    or_operator_str = f'or({", ".join(atom_str_list)})'
                    # add the joined translation to the clause string
                    clause_str = f'{clause_str}, {or_operator_str}' if len(clause_str) else or_operator_str
                    # the entire (sub)clause was processed - switch to the other variable
                    var_str = '(T)' if var_str == "(T1)" else "(T1)"
                elif token_type == 'IMP':
                    # the implication tuple appears after all the atoms tuples, 
                    # so the current clause string is ready to be encapsulated in 'implies()' operator
                    clause_str = f'implies({clause_str})'
                elif token_type in ['FORALL', 'EXISTS']:
                    # the quantifier tuple appears at the end of the queue

                    # if the clause contained implication, it is a liveness clause in conditional form
                    if imp_tuple:
                        # add both quantifiers and variables relation string to the clause string
                        clause_str = f'forall([T], exists([T1], and(GTE(T, T1), {clause_str})))'
                    # else if the quantifier type is EXISTS, it is a liveness clause in general form
                    elif token_type == 'EXISTS':
                        # add existential quantifier to the clause string
                        clause_str = f'exists([T], {clause_str})'
                    # else if the quantifier type is FORALL, it is a safety clause
                    elif token_type == 'FORALL':
                        # add universal quantifier to the clause string
                        clause_str = f'forall([T], not({clause_str}))'

            # append the whole clasue string to the list of clauses strings
            clause_str_list.append(clause_str)

        # if format is not raw, add header and footer of formulas list and encapsulate each clause in 'formula()' keyword
        if not raw:
            clause_str_list = list(map(lambda c_str: f'formula({c_str}).', clause_str_list))
            if formula_info['imp_exists']:
                # if the formula contained any implication, additionally include the clause defining the greater-or-equal relation
                clause_str_list = ['list_of_formulae(axioms).\nformula(GTE(t2,t1)).'] + clause_str_list + ['end_of_list.']
            else:
                clause_str_list = ['list_of_formulae(axioms).'] + clause_str_list + ['end_of_list.']

        # convert the list of all atoms to a dictionary to eliminate atoms repetitions and add the arity of atoms predicates
        all_atoms = dict.fromkeys(formula_info['all_atoms'], 1)
        if formula_info['imp_exists']:
            # if there was an implication in the formula, include the grater-or-equal predicate with arity of 2
            all_atoms['GTE'] = 2
            # add functions representing constants used to define the greater-or-equal relation
            functions_str = 'functions[(t1,0), (t2,0)].'

        # create the predicates list based on the dictionary
        predicates = []
        for pred, pred_value in all_atoms.items():
            predicates.append(f'({pred}, {pred_value})')
        predicates_str = f'predicates[{", ".join(predicates)}].'
        
        # prepare list of symbols
        if formula_info['imp_exists']:
            symbols_str_list = [
                'list_of_symbols.',
                functions_str,
                predicates_str,
                'end_of_list.'
            ]
        else:
            symbols_str_list = [
                'list_of_symbols.',
                predicates_str,
                'end_of_list.'
            ]

        # build file name and path
        script_path = os.path.dirname(__file__)
        os_sep = os.sep
        file_name = f'{self.output_file_name}{file_name_appendix}spass.in'
        
        # the file is saved in a folder called "generated_files", located in the same folder as this script
        dir_path = f'{script_path}{os_sep}generated_files'
        if not os.path.exists(dir_path):
            os.mkdir(dir_path)

        path = f'{dir_path}{os_sep}{file_name}'

        # save clauses to file
        with open(path, "w+") as file:
            if not raw:
                # write the problem header
                file.write(f'begin_problem({self.output_file_name}).\n\n')
                # write the descriptions list
                file.write('\n'.join(descriptions))
                file.write('\n\n')
                # write the symbols list
                file.write('\n'.join(symbols_str_list))
                file.write('\n\n')
                # write clauses in Clause Normal Form
                file.write('\n'.join(clause_str_list))
                # write the problem footer
                file.write('\n\nend_problem.')
            else:
                # file format is raw: write clauses in Conjunctive Normal Form and nothing else
                file.write("and(" + ','.join(clause_str_list) + ")")

        return path

    def joinProver9FilesWithPattern(self, files_list, pattern, remove=True):
        # remove the file number from file name
        output_file_name = files_list[0][:-16] + "prover9.in"
        input_files_contents = []

        # load files contents into a list
        for input_file_name in files_list:
            with open(input_file_name, "r") as input_file:
                # they should contain one line
                for line in input_file:
                    input_files_contents.append(line)
                    break

        final_formula = []

        # process the elements in the pattern and add their respective translations to the formula string
        for item in pattern:
            if item.startswith("FILE"):
                # add file contents located in the list at the index equal to file number in item name
                final_formula.append(input_files_contents[int(item[-1])])
            elif item == "NOT":
                final_formula.append('-')
            elif item == "LP":
                final_formula.append('(')
            elif item == "RP":
                final_formula.append(')')
            elif item == "OR":
                final_formula.append(" | ")
            elif item == "AND":
                final_formula.append(" & ")
        # finish the string with dot
        final_formula.append('.')

        with open(output_file_name, "w") as file:
            # write memory limit
            file.write('assign(max_megs, 1024).\n\n')
            # write the header
            file.write('formulas(sos).\n\n')
            # write the joined formula string
            file.write(''.join(final_formula))
            # write the footer
            file.write('\n\nend_of_list.')

        # if flag was set, remove the partial formulas files
        if remove:
            for input_file_name in files_list:
                os.remove(input_file_name)

        return output_file_name

    def joinSPASSFilesWithPattern(self, files_list, pattern, remove=True):
        # remove the file number from file name
        output_file_name = files_list[0][:-14] + "spass.in"
        input_files_contents = []

        # load files contents into a list
        for input_file_name in files_list:
            with open(input_file_name, "r") as input_file:
                # they should contain one line
                for line in input_file:
                    input_files_contents.append(line)
                    break

        # prepare the list of descriptions
        descriptions = [
            'list_of_descriptions.',
            'name({*' + self.output_file_name + '*}).',
            'author({*Jakub Semczyszyn*}).',
            'status(unknown).',
            'description({*The problem was generated randomly*}).',
            'end_of_list.'
        ]

        # list of all atoms is built based on global atoms dictionary
        all_atoms = [(atom,1) for atom in self.atom_counting_dict.keys()]
        # check if any of the formulas contains an implication and set the flag accordingly
        imp_exists = False
        for content in input_files_contents:
            if content.find("implies") != -1:
                imp_exists = True
                break
        
        # if there was an implication, add proper predicate and functions
        if imp_exists:
            all_atoms.append(('GTE',2))
            functions_str = 'functions[(t1,0), (t2,0)].'

        # create predicates list
        predicates = []
        for (pred, pred_value) in all_atoms:
            predicates.append(f'({pred}, {pred_value})')
        predicates_str = f'predicates[{", ".join(predicates)}].'
        
        # create symbols list
        if imp_exists:
            symbols_str_list = [
                'list_of_symbols.',
                functions_str,
                predicates_str,
                'end_of_list.'
            ]
        else:
            symbols_str_list = [
                'list_of_symbols.',
                predicates_str,
                'end_of_list.'
            ]

        final_formula = []

        # process the elements in the pattern
        insert_rp_in_next_step = False      # a flag informing whether a parenthesis closing a 'not' operator should be inserted
        beginnings_of_scopes_stack = [0]    # a stack containing indexes of scopes beginnings in final formula list
        remembered_rp = 0                   # a counter of remembered closing parentheses yet to be inserted
        operator_stack = []                 # a stack of conjunction and disjunction operators
        for item_index in range(len(pattern)):
            if pattern[item_index].startswith("FILE"):
                # add file contents located in the list at the index equal to file number in item name
                final_formula.append(input_files_contents[int(pattern[item_index][-1])])
                if insert_rp_in_next_step:
                    # previous item was 'not(', encapsulating the file contents, close it's parenthesis
                    final_formula.append(")")
                    # reset the flag
                    insert_rp_in_next_step = False
                else:
                    # the scope beginning was not saved in previous step, save it now 
                    beginnings_of_scopes_stack.append(len(final_formula) - 1)
            elif pattern[item_index] == "NOT":
                # add the opening of 'not()' operator
                final_formula.append("not(")
                # if it negates a single file contents
                if not pattern[item_index+1] == "LP":
                    # save the beginning of the negated file scope
                    beginnings_of_scopes_stack.append(len(final_formula) - 1)
                    # demand closing of the 'not()' operator in next step
                    insert_rp_in_next_step = True
                # else it negates a larger expression
                else:
                    # remember a closing parenthesis of 'not()' operator
                    remembered_rp += 1
            elif pattern[item_index] == "LP":
                # save a new beginning of the scope of a larger expression
                if final_formula:
                    beginnings_of_scopes_stack.append(len(final_formula) - 1)
                else:
                    beginnings_of_scopes_stack.append(0)
            elif pattern[item_index] == "RP":
                # close all remembered scopes by writing their parentheses and removing their beginnings from the stack
                while remembered_rp:
                    remembered_rp -= 1
                    final_formula.append(')')
                    beginnings_of_scopes_stack.pop()
                # close the scope of a larger expression
                beginnings_of_scopes_stack.pop()
                # the scope of the most recent operator must have ended - remove it from operators stack
                operator_stack.pop()
            elif pattern[item_index] in ["OR","AND"]:
                # if the operator is equal to the one on the top of the stack
                if operator_stack and operator_stack[-1] == pattern[item_index]:
                    # it means that it is the continuation of previous (dis/con)junction, 
                    # simply insert a comma to seperate elements
                    final_formula.append(',')
                else:
                    # insert a proper operator opening at the beginning of the most recent scope and add the operator to operators stack
                    if pattern[item_index] == "OR":
                        final_formula.insert(beginnings_of_scopes_stack[-1],"or(")
                        operator_stack.append("OR")
                    else:
                        final_formula.insert(beginnings_of_scopes_stack[-1],"and(")
                        operator_stack.append("AND")
                    # copy the most recent scope beginning, because it marks the beginnig of operator scope beginning as well now
                    beginnings_of_scopes_stack.append(beginnings_of_scopes_stack[-1])
                    # increment the next scope beginning (as the operator was inserted before it, moving it to a higher index)
                    # TODO: it may be necessary to increment more values for more complicated patterns
                    # (with nesting level >2) but for current set of patterns the algorithm works just fine
                    # UPDATE: value of LP-scope should also be increased in certain cases
                    beginnings_of_scopes_stack[-1] += 1
                    # there already is one element in the formula to wich the new operator applies, seperate it from the next ones with a comma
                    final_formula.append(',')
                    # remember the closing parenthesis of the operator
                    remembered_rp += 1
                
                # if the operator was inserted before the last element of the formula
                if beginnings_of_scopes_stack[-1] == len(final_formula) - 2:
                    # the scope of that element will no longer be needed and can be removed
                    beginnings_of_scopes_stack.pop()
        # write any remaining remembered parentheses
        while remembered_rp:
	        remembered_rp -= 1
	        final_formula.append(')')
        # write the closing parenthesis of 'formula()' keyword
        final_formula.append(').')

        # add header and footer of formulas list and beggining of the 'formula()' keyword to formula
        if imp_exists:
            # if the formula contained any implication, additionally include the clause defining the greater-or-equal relation
            final_formula[0] = "list_of_formulae(axioms).\nformula(GTE(t2,t1)).\nformula(" + final_formula[0]
        else:
            final_formula[0] = "list_of_formulae(axioms).\nformula(" + final_formula[0]
        final_formula.append("\nend_of_list.")

        with open(output_file_name, "w") as file:
            # write problem header
            file.write(f'begin_problem({self.output_file_name}).\n\n')
            # write descriptions list
            file.write('\n'.join(descriptions))
            file.write('\n\n')
            # write symbols list
            file.write('\n'.join(symbols_str_list))
            file.write('\n\n')
            # write formula string
            file.write(''.join(final_formula))
            # write problem footer
            file.write('\n\nend_problem.')

        # if flag was set, remove the partial formulas files
        if remove:
            for input_file_name in files_list:
                os.remove(input_file_name)

        return output_file_name

    # Diagnostic
    def printFormula(self, source=[]):
        # default source is the first formula
        if not source:
            source = self.formula
        # print each clause as list of tokens separated by spaces, encapsulated in square brackets
        for clause in source:
            print('[', end=' ')
            for token in clause:
                print(token, end=' ')
            print(']')

    def saveFormulas(self):
        # for each generated formula create a seperate file and
        # write each clause as list of tokens separated by spaces, encapsulated in square brackets
        if self.formula:
            with open(f'{os.path.dirname(__file__)}{os.sep}generated_files{os.sep}{self.output_file_name}_F1_generated.txt', "w") as file:
                for clause in self.formula:
                    file.write('[ ')
                    for token in clause:
                        file.write(str(token) + ' ')
                    file.write(']\n')
        if self.formula2:
            with open(f'{os.path.dirname(__file__)}{os.sep}generated_files{os.sep}{self.output_file_name}_F2_generated.txt', "w") as file:
                for clause in self.formula2:
                    file.write('[ ')
                    for token in clause:
                        file.write(str(token) + ' ')
                    file.write(']\n')
        if self.formula3:
            with open(f'{os.path.dirname(__file__)}{os.sep}generated_files{os.sep}{self.output_file_name}_F3_generated.txt', "w") as file:
                for clause in self.formula3:
                    file.write('[ ')
                    for token in clause:
                        file.write(str(token) + ' ')
                    file.write(']\n')
        if self.formulaR:
            with open(f'{os.path.dirname(__file__)}{os.sep}generated_files{os.sep}{self.output_file_name}_FR_generated.txt', "w") as file:
                for clause in self.formulaR:
                    file.write('[ ')
                    for token in clause:
                        file.write(str(token) + ' ')
                    file.write(']\n')

    def __getAtomStr(self, token, time_str):
        # translate an atom token to SPASS format
        # get atom name and join it with given argument variable
        token_str = f'{token.Value}{time_str}'
        if token.Negate:
            # if token is negated, encapsulate it in 'not()' operator
            token_str = f'not({token_str})'
        return token_str
