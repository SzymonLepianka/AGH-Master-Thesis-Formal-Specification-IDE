from datetime import datetime

class RFGError(RuntimeError):
    def __init__(self, message, caller):
        # save the error message and the formula parameters which caused it
        self.message = message
        self.caller = caller

    def __str__(self):
        # write the time of error occurence, the formula parameters that caused it and the message
        return datetime.now().strftime("%H:%M:%S") + ' | ' + self.caller + ' | ' + self.message + '\n'

class RFGTimeoutError(RFGError):
    def __init__(self, caller):
        # set a default message and save the formula parameters which caused it
        self.message = "ProverRunner.performMeasurements: prover9 timed out."
        self.caller = caller