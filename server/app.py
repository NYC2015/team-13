from factual import Factual
from flask import Flask, jsonify
import os
from firebase import firebase


app = Flask(__name__)

@app.route('/')
def index():
    return "We feed children!"

@app.route('/get_by_ean/<ean_13>', methods=['GET'])
def get_product_by_ean(ean_13):
    factual = Factual('pFMeueZJQpyZnSPILemUPxzNJmathJyrxoqOnow0', 'ROHZOgzy9GwJGb9egKpzAVTYZq35iuj6f3Uu4rNu')
    nutrition = factual.table('products-cpg-nutrition').search(ean_13)
    resp = nutrition.data()[0]
    return jsonify(**resp)

@app.route('/get_by_ean/<org_id>/<ean_13>', methods=['GET'])
def get_product_by_ean_for_org(org_id, ean_13):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None and ean_13 in exists:
        result = exists[ean_13]
        return jsonify(result)
    return ""

@app.route('/get_org_products/<org_id>', methods=['GET'])
def get_product_for_org(org_id):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None:
        return jsonify(exists)
    return ""

@app.route('/get_reservations/<user_id>', methods=['GET'])
def get_reservations_for_user(user_id):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    factual = Factual('pFMeueZJQpyZnSPILemUPxzNJmathJyrxoqOnow0', 'ROHZOgzy9GwJGb9egKpzAVTYZq35iuj6f3Uu4rNu')
    exists = fb.get("/reservations/" + user_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None:
        for org in exists:
            for ean in exists[org]:
                quantity = exists[org][ean]
                nutrition = factual.table('products-cpg-nutrition').search(ean)
                resp = nutrition.data()[0]
                exists[org][ean] = {'quantity' : quantity, 'metadata' : resp}
        return jsonify(exists)
    return ""

@app.route('/push_product/<org_id>/<ean_13>/<int:quantity>', methods=['GET'])
def push_product(org_id, ean_13, quantity):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None and ean_13 in exists:
        result = fb.put('/inventory/' + org_id + '/' + ean_13, 'quantity', str(int(exists[ean_13]['quantity']) + quantity), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
        return str(result)
    factual = Factual('pFMeueZJQpyZnSPILemUPxzNJmathJyrxoqOnow0', 'ROHZOgzy9GwJGb9egKpzAVTYZq35iuj6f3Uu4rNu')
    nutrition = factual.table('products-cpg-nutrition').search(ean_13)
    result = fb.put('/inventory/' + org_id, ean_13, {'quantity': str(quantity), 'metadata' : nutrition.data()[0], 'reserved' : str(0)}, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    return str(result)

@app.route('/reserve/<org_id>/<ean_13>/<user_id>/<int:quantity>', methods=['GET'])
def reserve(org_id, ean_13, user_id, quantity):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None and ean_13 in exists:
        inventory = int(exists[ean_13]["quantity"])
        reserved = int(exists[ean_13]["reserved"])
        if quantity <= inventory - reserved:
            user_exists = fb.get("/inventory/" + org_id + "/" + ean_13 + "/reservers", None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            if user_exists is not None and user_id in user_exists:
                curr_reserve = int(user_exists[user_id]['quantity'])
                result = fb.put("/inventory/" + org_id + "/" + ean_13, "reserved", str(reserved + quantity), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
                result = fb.put("/reservations/" + user_id + "/" + org_id + "/", ean_13, str(quantity + curr_reserve), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
                result = fb.put("/inventory/" + org_id + "/" + ean_13 + "/reservers", user_id, {'quantity': str(quantity + curr_reserve)}, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
                return str(result)
            result = fb.put("/inventory/" + org_id + "/" + ean_13, "reserved", str(reserved + quantity), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            result = fb.put("/reservations/" + user_id + "/" + org_id + "/", ean_13, str(quantity), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            result = fb.put("/inventory/" + org_id + "/" + ean_13 + "/reservers", user_id, {'quantity': str(quantity)}, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            return str(result)
        return ""
    return ""

@app.route('/cancel/<org_id>/<ean_13>/<user_id>', methods=['GET'])
def cancel(org_id, ean_13, user_id):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None and ean_13 in exists:
        user_exists = fb.get("/inventory/" + org_id + "/" + ean_13 + "/reservers", None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
        if user_exists is not None and user_id in user_exists:
            curr_reserve = int(user_exists[user_id]['quantity'])
            fb.put("/inventory/" + org_id + "/" + ean_13, "reserved", str(int(exists[ean_13]['reserved']) - curr_reserve), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            result = fb.delete("/reservations/" + user_id + "/" + org_id + "/", ean_13, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            result = fb.delete("/inventory/" + org_id + "/" + ean_13 + "/reservers", user_id, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            return str(result)
        return ""
    return ""

@app.route('/deliver/<org_id>/<ean_13>/<user_id>', methods=['GET'])
def deliver(org_id, ean_13, user_id):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None and ean_13 in exists:
        user_exists = fb.get("/inventory/" + org_id + "/" + ean_13 + "/reservers", None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
        if user_exists is not None and user_id in user_exists:
            curr_reserve = int(user_exists[user_id]['quantity'])
            fb.put("/inventory/" + org_id + "/" + ean_13, "quantity", str(int(exists[ean_13]['quantity']) - curr_reserve), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            fb.put("/inventory/" + org_id + "/" + ean_13, "reserved", str(int(exists[ean_13]['reserved']) - curr_reserve), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            result = fb.delete("/reservations/" + user_id + "/" + org_id + "/", ean_13, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            result = fb.delete("/inventory/" + org_id + "/" + ean_13 + "/reservers", user_id, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
            return str(result)
        return ""
    return ""





if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    if port == 5000:
        app.debug = True
    app.run(host='0.0.0.0', port=port)
