class LogicToken:
    # available token types for relations
    relation_types = ["OR"]#,"AND","BACKIMP","EQUI"]
    # available token types for quantifiers, operators and atoms
    special_types = ["EXISTS","FORALL","ATOM","IMP"]

    def __init__(self, tokentype, value=None, negate=None):
        # save type of token
        self.TokenType = tokentype
        # if token type not recognised, raise an error
        if (tokentype not in self.relation_types and tokentype not in self.special_types):
            raise RuntimeError("LogicToken.__init__: Token type not accepted.")
        # if token has type ATOM but no data given, raise an error
        if (tokentype == "ATOM" and (value == None or negate == None)):
            raise RuntimeError("LogicToken.__init__: ATOM token must have a value and negation flag set.")
        # save value - it is atom name in case of ATOM token
        self.Value = value
        # save negation flag - it tells wether to negate the atom
        self.Negate = negate
    
    def __str__(self):
        # convert the atom to Prover9 format, which is also the easiest human-readable format
        if self.TokenType == "ATOM":
            result = self.Value
            if self.Negate:
                result = '-' + result
        # in case of any other token type, return just the type
        else:
            result = self.TokenType
        return result


def zipToList(list1, list2):
    # zip two lists into one (put elements from two lists into one alternately);
    # if one list is longer than the other, the excessive elements of the longer one will be placed at the end, in order
    result = []
    mem = 0
    for index in range(len(list1)):
        result.append(list1[index])
        if index < len(list2) :
            result.append(list2[index])
            mem = index
    if(len(list2) > len(list1)):
        for index in range(mem+1,len(list2)):
            result.append(list2[index])
    return result

def readFormulaFile(path):
    formula = []
    with open(path, "r") as file:
        for line in file:
            # omit the square brackets at the beginning and the end
            tokens = line.split(" ")[1:-1]
            # add a new empty clause to the formula
            formula.append([])
            # read token strings and turn them back into tokens, add them to the newest clause in formula
            for elem in tokens:
                if elem in LogicToken.relation_types or elem in LogicToken.special_types:
                    formula[-1].append(LogicToken(elem))
                elif elem.startswith("var"):
                    formula[-1].append(LogicToken("ATOM",value=elem,negate=False))
                elif elem.startswith("-var"):
                    formula[-1].append(LogicToken("ATOM",value=elem,negate=True))
                else:
                    raise RuntimeError(f'readFormulaFile: Invalid token - {elem}')
    return formula