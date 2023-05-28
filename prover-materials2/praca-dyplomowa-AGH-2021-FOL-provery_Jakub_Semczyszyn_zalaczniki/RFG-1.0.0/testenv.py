from randomgen import Generator
from customerrors import RFGError, RFGTimeoutError
import provrun
import csv
import os
import subprocess
from statistics import mean, mode


class TestEnv:
    def __init__(self, config_file_path):
        # initiate a list of test cases
        self.testCases = []
        # read the scenario file
        self.readTestConfigFile(config_file_path)

    def makeTests(self):
        # open a file to log errors into
        with open('./generated_files/error_log.txt','a') as erlog:
            # open a .csv file for results
            with open('./generated_files/test_session_results.csv', 'w', newline='') as csvfile:
                # setup CSV writer with comma separator and standard quotes character
                results_writer = csv.writer(csvfile, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
                # write column names row
                results_writer.writerow(["Problem","Number of atoms","Precentage of safety clauses", \
                    "Clauses lengths","Number of clauses", "Distribution of lengths", "Satisfiability", \
                    "Memory used by prover9","Time used by prover9","Memory used by SPASS","Time used by SPASS"])
                for case in self.testCases:
                    try:
                        # create Generator object for current test case (this also cause formula to be generated and saved to a text file)
                        testObject = case.createGenerator()
                        # translate the formula to Prover9 and SPASS Prover format and save the translations to files
                        self.filenames = testObject.translateAndSave()
                    except RFGError as rfg_err:
                        # log any errors to error log file
                        erlog.write(str(rfg_err))

                    try:
                        # execute Prover9 and get results
                        prover9_stats = self.runProver("prover9")
                    except RFGTimeoutError as tm_err:
                        # if process was terminated due to timeout, set the results manually
                        prover9_stats = {"sat": "Timeout", "memory": 0, "time": 300}
                        # write appropiate information to error log file                   
                        erlog.write(str(tm_err))
                    except RFGError as rfg_err:
                        # log any other errors to error log file
                        erlog.write(str(rfg_err))

                    try:
                        # execute SPASS and get results
                        spass_stats = self.runProver("spass")

                        # if both provers got a valid result and the results are different, raise an error
                        if spass_stats["sat"] in ["True", "False"] and prover9_stats["sat"] in ["True", "False"] and prover9_stats["sat"] != spass_stats["sat"]:
                            raise RFGError(f'TestEnv.makeTests: Provers got different results. Spass: {spass_stats["sat"]}; Prover9: {prover9_stats["sat"]}', self.filenames["output"])

                        # if any prover exceeded the memory limit, set "Memory limit" result
                        if prover9_stats["sat"] == "Memory limit" or spass_stats["sat"] == "Memory limit":
                            sat_result = "Memory limit"
                        # else if any prover exceeded the time limit, set "Timeout" result
                        elif prover9_stats["sat"] == "Timeout" or spass_stats["sat"] == "Timeout":
                            sat_result = "Timeout"
                        # else - they both got the same valid result, save it
                        else:
                            sat_result = prover9_stats["sat"]
                        
                        # read parameters, representing CSV columns, from test case object and save them to a list
                        test_stats = [case.parameters_list[0]]
                        test_stats.append(round(case.parameters_list[3]*case.parameters_list[5]))
                        test_stats.append(case.parameters_list[1])
                        cl_string = ""
                        for cl in case.parameters_list[2]:
                            cl_string += str(cl) + ", "
                        test_stats.append(cl_string[:-2])
                        test_stats.append(case.parameters_list[3])
                        if (case.parameters_list[4] and case.parameters_list[0] in \
                            ["problem6","problem7a","problem7b","problem8a","problem8b","problem8c"]) \
                            or case.parameters_list[0] == "problem2":
                            test_stats.append("poisson")
                        else:
                            test_stats.append(case.parameters_list[6])
                        test_stats.append(sat_result)
                        test_stats.append(prover9_stats["memory"])
                        test_stats.append(prover9_stats["time"])
                        test_stats.append(spass_stats["memory"])
                        test_stats.append(spass_stats["time"])

                        # write the list as a row to CSV
                        results_writer.writerow(test_stats)

                        # kill all prover processes that did not end by themselves
                        subprocess.call('pkill -f "prover9|SPASS"', shell=True)
                    except RFGError as rfg_err:
                        # log any errors to error log file
                        erlog.write(str(rfg_err))
        print("\n===================\n""Testing finished. See test_session_results.csv for results or error_log.txt for encountered errors.\n=== TEST ENV FINISH ===\n")


    def runProver(self, prover_name):
        memory_usages = []
        time_usages = []
        result_list = []

        # create a ProverRunner object with a file corresponding to the prover
        runner = provrun.ProverRunner(prover_name, input_file=self.filenames[prover_name + "_input"])
        # perform three tests on the same formula to get more truthful results
        for attempt in range(1, 4):
            # include the attempt number in output file name
            current_output_file = os.path.dirname(__file__) + "/generated_files/" + self.filenames["output"] + \
                "_attempt" + str(attempt) + "_" + prover_name + ".out"
            # run prover and read output file
            usage_dict = runner.performMeasurements(current_output_file)
            # save results in lists
            memory_usages.append(usage_dict["memory"])
            time_usages.append(usage_dict["time"])
            result_list.append(usage_dict["sat"])

        for result_idx in range(1, len(result_list)):
            # if there are two contrary valid results in the satisfiability list, raise an error
            if result_list[result_idx] in ["True","False"] and result_list[result_idx - 1] in ["True","False"] and  result_list[result_idx] != result_list[result_idx - 1]:
                raise RFGError("TestEnv.runProver: Different results for the same formula.", self.filenames["output"])

        # return the mean of time and memory results 
        # and the mode of satisfiability results (this way, for example, if only one attempt gets timeout, the result will still be valid)
        return {"memory": mean(memory_usages), "time": mean(time_usages), "sat": mode(result_list)}

    def readTestConfigFile(self, path):
        with open(path, 'r') as config:
            current_case_maker = None
            for line in config:
                # remove endline character
                line = line[:-1]
                # if no problem is currently processed
                if not current_case_maker:
                    # look for beginning of problem block
                    if line.startswith("problem"):
                        # setup a new case maker and set the problem type based on block header
                        current_case_maker = CaseMaker()
                        current_case_maker.test_type = line
                # if a problem block is being processed
                else:
                    if line == "end of problem":
                        # the problem block has ended
                        with open('./generated_files/error_log.txt','a') as erlog:
                            try:
                                # generate test cases with current case maker and add them to test cases list
                                new_cases = current_case_maker.makeCases()
                                self.testCases = self.testCases + new_cases
                            except RFGError as rfg_err:
                                erlog.write(str(rfg_err)) 

                        # remove the case maker
                        current_case_maker = None
                    # else, the first word in a line should be parameter name and the next ones the parameter values
                    # save them in current case maker
                    elif line.startswith("safety_prec"):
                        prec = line.split(" ")[1:]
                        current_case_maker.precentages = [int(p) for p in prec]
                    elif line.startswith("lengths"):
                        lengths = line.split(" ")[1:]
                        current_case_maker.clause_lengths = [int(length) for length in lengths]
                    elif line.startswith("clauses"):
                        numbers = line.split(" ")[1:]
                        current_case_maker.clauses_num = [int(number) for number in numbers]
                    elif line.startswith("poisson"):
                        poisson = line.split(" ")[1]
                        if poisson == "True":
                            current_case_maker.poisson = True
                        elif poisson == "False":
                            current_case_maker.poisson = False
                    elif line.startswith("n_coeff"):
                        coeffs = line.split(" ")[1:]
                        current_case_maker.atoms_num_coeff = [float(n) for n in coeffs]
                    elif line.startswith("distribution"):
                        current_case_maker.problem5_distribution = line.split(" ")[1:]
                    elif line.startswith("lambda"):
                        current_case_maker.lambda_value = float(line.split(" ")[1])

class CaseMaker:
    # inititate with default parameters values
    test_type = None
    precentages = [50]
    clause_lengths = [2,3,4,6,8,10]
    clauses_num = [50]
    poisson = False
    atoms_num_coeff = [0.5]
    problem5_distribution = ["even"]
    lambda_value = 3.5

    def makeCases(self):
        cases = []
        # create test cases by breaking lists of parameters into different cases
        for prec in self.precentages:
            # if problem is of type problem4, break clauses lengths list into seprate cases
            if self.test_type == "problem4":
                parameters_pt1 = [self.test_type, prec]
                for length in self.clause_lengths:
                    parameters_pt2 = [[length]]
                    for number in self.clauses_num:
                        parameters_pt3 = [number, self.poisson]
                        for n in self.atoms_num_coeff:
                            parameters_pt4 = [n]
                            for dist in self.problem5_distribution:
                                parameters_pt5 = [dist, self.lambda_value]
                                cases.append(Case(parameters_pt1 + parameters_pt2 + parameters_pt3 + parameters_pt4 + parameters_pt5))
            # for any other problem, treat clause lengths list as a one object, used in every case
            else:
                parameters_pt1 = [self.test_type, prec, self.clause_lengths]
                for number in self.clauses_num:
                    parameters_pt2 = [number, self.poisson]
                    for n in self.atoms_num_coeff:
                        parameters_pt3 = [n]
                        for dist in self.problem5_distribution:
                            parameters_pt4 = [dist, self.lambda_value]
                            cases.append(Case(parameters_pt1 + parameters_pt2 + parameters_pt3 + parameters_pt4))
        return cases


class Case:
    def __init__(self, parameters_list):
        self.parameters_list = parameters_list

    def createGenerator(self):
        # print info about currently processed formula
        print(self.parameters_list)
        # create Generator using parameters list
        return Generator(*self.parameters_list)
        