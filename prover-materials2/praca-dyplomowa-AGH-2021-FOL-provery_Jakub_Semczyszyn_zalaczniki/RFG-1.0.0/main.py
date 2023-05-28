from provrun import ProverRunner
from randomgen import Generator
from testenv import TestEnv
import os

enviroment = TestEnv(os.path.dirname(__file__) + "/dane_testowe/scenario")
enviroment.makeTests()

# test = Generator('problem3', clauses_num=2000, atoms_num_coeff=3)
# files = test.translateAndSave()
# runner = ProverRunner("prover9", input_file=files["prover9_input"])
# current_output_file = files["prover9_input"][:-2] + "out"
# usage_dict = runner.performMeasurements(current_output_file)
# print(usage_dict)