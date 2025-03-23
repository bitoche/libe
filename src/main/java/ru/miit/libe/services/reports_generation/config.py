import dotenv
from os import getenv as env
dotenv.load_dotenv()

def watch_env_params():
    return_str = 'db_config:\n{'
    for key, val in DB_CONFIG.items():
        return_str += f'\n\'{str(key)}\': \'{str(val)}\','
    return_str += '\n}\n'
    return return_str
    
DB_CONFIG = {
    'HOST': str(env('DB_HOST')),
    'PORT': str(env('DB_PORT')),
    'USER': str(env('DB_USERNAME')),
    'PASS': str(env('DB_PASSWORD')),
    'NAME': str(env('DB_NAME')),
    'REPORTS_SCHEMA': str(env('DB_REPORTS_SCHEMA'))
}