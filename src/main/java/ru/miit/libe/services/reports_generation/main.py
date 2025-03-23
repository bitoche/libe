from flask import Flask, request
import logging
from threading import Thread
import service

app = Flask(__name__)

class Response():
    def __init__(self, status:str, message:str):
        self.status = status
        self.message = message
    def get(self):
        return {
            'status': self.status,
            'message': self.message
        }

@app.route('/api/startReports', methods=['POST'])
def handle_request():
    try:
        params = request.get_json()
        print(f'recieved params = {params}')
        # Бизнес-логика обработки
        try:
            calc_id = int(params['calc_id'])
        except:
            print(f'calc_id not present. setted to default = 100')
            calc_id = 100
        try: 
            start_date = params['start_date']
        except:
            print(f'start_date not present. setted to default = 1990-01-01')
            start_date = '1990-01-01'
        try:
            end_date = params['end_date']
        except:
            print(f'start_date not present. setted to default = 2100-01-01')
            end_date = '2100-01-01'
        parsed_params = {
            'calc_id': calc_id,
            'start_date': start_date,
            'end_date': end_date
        }
        print(f'parsed params = {parsed_params}')
        
        # # главная функция
        calc_thread = Thread(target=service.start_reports,
                            args=[parsed_params])
        calc_thread.start()
        # не асинхронная версия
        # service.start_reports(params=parsed_params)
        
        return {'status': 'success', 'calc_id': calc_id}, 200
    except Exception as e:
        logging.error(f"Error processing request: {str(e)}")
        return {'status': 'error', 'message':str(e)}, 500
    
@app.route('/api/getReport', methods=['POST'])
def get_reports_by_id_request():
    try:
        params = request.get_json()
        print(f'recieved params = {params}')
        # Бизнес-логика обработки
        try:
            calc_id = int(params['calc_id'])
        except:
            return {'status': 'error', 'message': 'calc_id not present'}, 400
        
        try:
            report_name = params['report_name']
        except:
            print(f'report_name not present. setted to default = all')
            report_name = 'all'
        parsed_params = {
            'calc_id': calc_id,
            'report_name': report_name
        }
        print(f'parsed params = {parsed_params}')
        
        # не асинхронная версия
        response = service.get_report_by_id(params=parsed_params)
        
        return {'status': 'success', 'data': response}, 200
    except Exception as e:
        logging.error(f"Error processing request: {str(e)}")
        return {'status': 'error', 'message':str(e)}, 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8081)
