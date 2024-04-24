from flask import Flask, request, jsonify

app = Flask(__name__)

# Diccionario para almacenar la información del usuario
user_data = {
    'name': 'Gustavo Calderon',
    'email': 'john.doe@example.com',
    'rut': '12345678-9'
}

# Ruta para obtener la información del usuario
@app.route('/user', methods=['GET'])
def get_user():
    return jsonify(user_data)

# Ruta para actualizar la información del usuario
@app.route('/user/update', methods=['POST'])
def update_user():
    data = request.get_json()

    # Actualizar la información del usuario con los datos recibidos
    if 'name' in data:
        user_data['name'] = data['name']
    if 'email' in data:
        user_data['email'] = data['email']
    if 'rut' in data:
        user_data['rut'] = data['rut']

    return jsonify({'message': 'Información actualizada con éxito'}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
