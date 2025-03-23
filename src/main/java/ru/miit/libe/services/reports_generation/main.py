from flask import Flask, request
import logging
import service

app = Flask(__name__)

@app.route('/api/startReports', methods=['POST'])
def handle_request():
    try:
        params = request.get_json()
        
        # Бизнес-логика обработки
        result = process_parameters(params)
        
        return {'status': 'success', 'data': result}, 200
    except Exception as e:
        logging.error(f"Error processing request: {str(e)}")
        return {'status': 'error', 'message': str(e)}, 500

def process_parameters(params):
    print(f'recieved params = {params}')
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

    # главная функция
    service.start_reports(params=parsed_params)
    
    return {"processed": True}

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
