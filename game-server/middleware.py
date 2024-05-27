import subprocess

path = '/Users/noelkronenberg/Documents/GitHub' # NOTE: adjist to local path

def getMove(fen, time_limit=None):
    java_class_path = f'{path}/projekt-ki/jump-sturdy-ai/target/classes'
    java_command = ['java', '-cp', java_class_path, 'communication.Middleware', fen]
    
    if time_limit is not None:
        java_command.append(str(time_limit))
    
    process = subprocess.Popen(java_command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    output, _ = process.communicate()
    
    return output.decode().strip()