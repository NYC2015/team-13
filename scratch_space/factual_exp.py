from factual import Factual
factual = Factual('pFMeueZJQpyZnSPILemUPxzNJmathJyrxoqOnow0', 'ROHZOgzy9GwJGb9egKpzAVTYZq35iuj6f3Uu4rNu')
nutrition = factual.table('products-cpg-nutrition').search('0024100101481')
print nutrition.data()

nutrition = factual.table('products-cpg-nutrition')