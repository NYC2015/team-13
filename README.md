# JP Morgan Code for Good Challenge in New York

This is team 13 - Codestantiate! 

We are a team from University at Buffalo and MIT!

## Team Members

Jarret Hutchison 

Winnie Liang 

Mengpei Hu 

Chern Yee Chua 

Linda Jing 

Nikhil Buduma

## API

### Get metadata by EAN

```
/get_by_ean/<ean_13>
```

Given product defined by `ean_13`, returns an instance of our Nutrition Facts Schema (see below)

### Get product information by EAN for an organization

```
/get_by_ean/<org_id>/<ean_13>
```

Given an organization's `org_id` and a product's `ean_13`, we return a json blob of the following format:

```
{
	"metadata" : <NutritionFactsSchema>,
	"quantity" : <int>,
	"reserved" : <int>,
	"reservers" : {
		<user1> : {
			"quantity" : <int>
		},
		<user2> : {
			"quantity" : <int>
		},
		...
	}
}
```

### Get all products of an organization

```
/get_org_products/<org_id>
```

Given an an organization's `org_id`, we produce a json blob as follows

```
{
	<ean1> : {
		"metadata" : <NutritionFactsSchema>,
		"quantity" : <int>,
		"reserved" : <int>,
		"reservers" : {
			<user1> : {
				"quantity" : <int>
			},
			<user2> : {
				"quantity" : <int>
			},
			...
		}
	},
	<ean2> : {
		"metadata" : <NutritionFactsSchema>,
		"quantity" : <int>,
		"reserved" : <int>,
		"reservers" : {
			<user1> : {
				"quantity" : <int>
			},
			<user2> : {
				"quantity" : <int>
			},
			...
		}
	},
	...
}
```

### Add products to an organization's inventory

```
/push_product/<org_id>/<ean_13>/<int:quantity>
```

Adds `quantity` units of product `ean_13` to the inventory of organization `org_id`

### Make reservation for user

```
/reserve/<org_id>/<ean_13>/<user_id>/<int:quantity>
```

Reserve `quantity` units of product `ean_13` from the inventory of organization `org_id` for user `user_id`

### Cancel reservation for user

```
/cancel/<org_id>/<ean_13>/<user_id>
```

Cancel reservation for product `ean_13` from organization `org_id` for user `user_id`

### Deliver product to user

```
/deliver/<org_id>/<ean_13>/<user_id>
```

Deliver on reservation for product `ean_13` from organization `org_id` for user `user_id`

## Miscellaneous Notes

### Product Categories

```
Baby Food
Baking Ingredients
Bread
Breakfast Foods
Butters
Canned Food
Canned Fruits & Vegetables
Cheeses
Chips
Cookies
Dips
Eggs
Energy Drinks
Flours
Frozen Foods
Fruits
Grains
Granola Bars
Ice Cream & Frozen Desserts
Jams & Jellies
Juices
Lentils
Meat Alternatives
Meat, Poultry, Seafood Products
Milk & Milk Substitutes
Noodles & Pasta
Nutritional Bars, Drinks, and Shakes
Nuts
Packaged Foods
Popcorn
Prepared Meals
Pudding
Rice
Snacks
Soda
Soups & Stocks
Sugars & Sweeteners
Syrups
Vegetables
Vinegars
Vitamins & Supplements
Wheat Flours & Meals
Yogurt
```

### Nutrition Facts Schema

```
{
	u'ean13': u'0024100101481', 
	u'sugars': 1, 
	u'trans_fat': 0, 
	u'protein': 3, 
	u'size': [u'13 oz'], 
	u'category': u'Snacks', 
	u'ingredients': [u'Cheese Crackers (Enriched Flour (Wheat Flour', u'Niacin', u'Reduced Iron', u'Thiamin Mononitrate (Vitamin B1)', u'Riboflavin (Vitamin B2)', u'Folic Acid', u'Vegetable Oil (Canola', u'Cottonseed', u'Palm', u'Sunflower'], 
	u'vitamin_c': 0, 
	u'vitamin_a': 0, 
	u'dietary_fiber': 1, 
	u'product_name': u'Snack Mix Baked Snack Assortment', 
	u'brand': u'Cheez It', u'cholesterol': 0, 
	u'image_urls': [u'http://Img1.targetimg1.com/wcsstore/TargetSAS//img/p/13/00/13009791_201307191112.jpg', u'http://c3.soap.com/images/products/p/p/zqb/zqb-5635_1.jpg', u'http://ecx.images-amazon.com/images/I/51khH2ynOxL._SL500_AA300_PIbundle-4,TopRight,0,0_AA300_SH20_.jpg', u'http://media.shopwell.com/product/2410092464_full.jpg', u'http://www.scanavert.com/api/picture.php?upc=00024100101481&width=150&height=150'], 
	u'factual_id': u'a0799ad5-aa75-40d3-bd1e-56a76ea3b1d7', 
	u'avg_price': 7.39, 
	u'potassium': 0, 
	u'fat_calories': 40, 
	u'sodium': 290, 
	u'calories': 120, 
	u'upc': u'024100101481', 
	u'sat_fat': 1, 
	u'calcium': 2, 
	u'total_fat': 4.5, 
	u'serving_size': u'0.5 cup', 
	u'iron': 6, 
	u'servings': 12, 
	u'total_carb': 21
}
```
