from factual import Factual
from flask import Flask, jsonify
import os

app = Flask(__name__)

@app.route('/')
def index():
    return "We feed children!"

@app.route('/get_by_ean/<ean_13>', methods=['GET'])
def get_product_by_ean(ean_13):
    factual = Factual('pFMeueZJQpyZnSPILemUPxzNJmathJyrxoqOnow0', 'ROHZOgzy9GwJGb9egKpzAVTYZq35iuj6f3Uu4rNu')
    nutrition = factual.table('products-cpg-nutrition').search(ean_13)
    print nutrition.data()
    resp = nutrition.data()[0]
    return jsonify(**resp)


if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    if port == 5000:
        app.debug = True
    app.run(host='0.0.0.0', port=port)
