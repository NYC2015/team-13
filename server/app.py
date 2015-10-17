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

@app.route('/push_product/<org_id>/<ean_13>/<int:quantity>', methods=['GET'])
def push_product(org_id, ean_13, quantity):
    fb = firebase.FirebaseApplication('https://feedthechildren.firebaseio.com/', None)
    exists = fb.get("/inventory/" + org_id, None, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    if exists is not None and ean_13 in exists:
        result = fb.put('/inventory/' + org_id + '/' + ean_13, 'quantity', str(int(exists[ean_13]['quantity']) + quantity), params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
        return str(result)
    factual = Factual('pFMeueZJQpyZnSPILemUPxzNJmathJyrxoqOnow0', 'ROHZOgzy9GwJGb9egKpzAVTYZq35iuj6f3Uu4rNu')
    nutrition = factual.table('products-cpg-nutrition').search(ean_13)
    result = fb.put('/inventory/' + org_id, ean_13, {'quantity': str(quantity), 'metadata' : nutrition.data()[0]}, params={'print': 'pretty'}, headers={'X_FANCY_HEADER': 'VERY FANCY'})
    return str(result)



if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    if port == 5000:
        app.debug = True
    app.run(host='0.0.0.0', port=port)
