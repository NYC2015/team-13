# -*- coding: utf-8 -*-

import json
import unittest

from factual import Factual
from factual.utils import circle, point
from .test_settings import KEY, SECRET

SANDBOX_UUID = '2aba7958-795c-4100-a221-b47df58a14ba'
FACTUAL_UUID = '03c26917-5d66-4de9-96bc-b13066173c65'

class FactualAPITestSuite(unittest.TestCase):
    def setUp(self):
        self.factual = Factual(KEY, SECRET)
        self.places = self.factual.table('places')
        self.facets = self.factual.facets('global')

    def test_search(self):
        q = self.places.search('factual')
        row = q.data()[0]
        self.assertRegexpMatches(row['name'], 'Factual')

    # full text search for "Mcdonald's, Santa Monica" (test spaces, commas, and apostrophes)
    def test_search2(self):
        q = self.places.search("McDonald's,Santa Monica").limit(20)
        included_rows = q.included_rows()
        self.assertEqual(20, included_rows)

    def test_limit(self):
        q = self.places.search('sushi').limit(3)
        self.assertEqual(3, len(q.data()))

    def test_select(self):
        q = self.places.select('name,address')
        row = q.data()[0]
        self.assertEqual(2, len(row.keys()))

    def test_sort(self):
        q1 = self.places.sort_asc('name')
        self.assertTrue(q1.data()[0]['name'] < q1.data()[1]['name'])

        q2 = self.places.sort_desc('name')
        self.assertTrue(q2.data()[0]['name'] > q2.data()[1]['name'])

    def test_paging(self):
        q1 = self.places.offset(30)
        r1 = q1.data()[0]

        q2 = self.places.page(3, 15)
        r2 = q2.data()[0]
        self.assertEqual(r1['name'], r2['name'])

    def test_filters(self):
        q = self.places.filters({'region': 'NV'})
        for r in q.data():
            self.assertEqual('NV', r['region'])

    def test_geo(self):
        q = self.places.search('factual').geo(circle(34.06021, -118.41828, 1000))
        row = q.data()[0]
        self.assertEqual('Factual', row['name'])
        self.assertEqual('1999 Avenue Of The Stars', row['address'])

    def test_read_user(self):
        q = self.places.search('sushi').user('python_driver_tester')
        included_rows = q.included_rows()
        self.assertEqual(20, included_rows)

    def test_resolve(self):
        q = self.factual.resolve('places-us', {"name": "factual inc", "locality": "los angeles","postcode":"90067"})
        row = q.data()[0]
        self.assertTrue(row['resolved'])
        self.assertEqual('1999 Avenue Of The Stars', row['address'])

    def test_resolve_debug(self):
        q = self.factual.resolve('places-v3', {'name': 'factual inc', 'locality': 'los angeles'}, debug=True)
        row = q.data()[0]
        self.assertIn('similarity', row)

    def test_crosswalk(self):
        q = self.factual.crosswalk()
        result = q.filters({'factual_id':FACTUAL_UUID,'namespace':'facebook'}).data()
        self.assertGreaterEqual(len(result), 1)
        self.assertEqual('151202829333', result[0]['namespace_id'])

    def test_schema(self):
        schema = self.places.schema()
        self.assertGreater(len(schema['fields']),20)
        self.assertIn('title', schema)
        self.assertIn('locality', set(f['name'] for f in schema['fields']))

    # full text search for things where locality equals 大阪市 (Osaka: test unicode)
    def test_unicode(self):
        q = self.places.filters({'locality': u'大阪市'})
        for r in q.data():
            self.assertEqual(u'大阪市', r['locality'])

    def test_bw_encoding(self):
        q = self.places.filters({'name': {"$bw":"Star"}})
        row = q.data()[0]
        self.assertRegexpMatches(row['name'], "Star")

    def test_in(self):
        q = self.places.filters({"locality":{"$in":["Santa Monica","Los Angeles","Culver City"]}})
        row = q.data()[0]
        self.assertIn(row['locality'], ["Santa Monica","Los Angeles","Culver City"])

    def test_and(self):
        q = self.places.filters({"$and":[{"country":"US"},{"website":{"$blank":False}}]})
        row = q.data()[0]
        self.assertEqual('US', row['country'].upper())
        self.assertRegexpMatches(row['website'], 'http')

    def test_includes(self):
        q = self.places.filters({'category_ids':{'$includes':10}})
        rows = q.data()
        for row in rows:
            self.assertIn(10, row['category_ids'])

    def test_raw_read(self):
        # filters here is url encoded {"name":"Starbucks"}
        response = self.factual.raw_read('t/places/read', 'limit=15&filters=%7B%22name%22%3A%22Starbucks%22%7D')
        payload = json.loads(response)
        data = payload['response']['data']
        self.assertEqual(15, payload['response']['included_rows'])
        self.assertTrue(all(row['name'] == 'Starbucks' for row in data))

    def test_raw_read_with_map(self):
        response = self.factual.raw_read('t/places/read', {'limit':15,'filters':{"name":"Starbucks"}})
        payload = json.loads(response)
        data = payload['response']['data']
        self.assertEqual(15, payload['response']['included_rows'])
        self.assertTrue(all(row['name'] == 'Starbucks' for row in data))

    def test_raw_write(self):
        uuid = SANDBOX_UUID
        params = {'problem':'other','user':'python_driver_tester','debug':True}
        response = self.factual.raw_write('t/us-sandbox/' + uuid + '/flag', params)
        payload = json.loads(response)
        self.assertEqual('ok', payload['status'])

    def test_facets1(self):
        q = self.facets.search("starbucks").select("country")
        results = q.data()['country']
        self.assertTrue(results['us'] > 5000)
        self.assertTrue(results['ca'] > 200)

    def test_facets2(self):
        q = self.facets.search("starbucks").select("locality,region").filters({"country":"US"})
        locality = q.data()['locality']
        region = q.data()['region']
        self.assertTrue(locality['chicago'] > 50)
        self.assertTrue(region['tx'] > 200)

    def test_facets_geo(self):
        q = self.facets.select("locality").geo(circle(34.06018, -118.41835, 1000))
        locality = q.data()['locality']
        self.assertTrue(locality['los angeles'] > 3000)
        self.assertTrue(locality['beverly hills'] > 500)

    def test_geocode(self):
        geocode = self.factual.geocode(point(34.058744, -118.416937))
        result = geocode.data()[0]
        self.assertEqual('1999 Avenue Of The Stars', result['address'])
        self.assertLess(result['$distance'], 20)

    def test_submit_without_id(self):
        values = {'longitude': 100}
        submit = self.factual.submit('us-sandbox', values=values).user('python_driver_tester')
        response = submit.write()
        if 'new_entity' in response:
            self.assertTrue(response['new_entity'])
        else:
            self.assertIn('status', response)
            self.assertEqual('warning', response['status'])

    def test_submit_with_id(self):
        values = {'longitude': 100}
        submit = self.factual.submit('us-sandbox', factual_id=SANDBOX_UUID, values=values).user('python_driver_tester')
        response = submit.write()
        if 'new_entity' in response:
            self.assertFalse(response['new_entity'])
        else:
            self.assertIn('status', response)
            self.assertEqual('warning', response['status'])

    def test_clear(self):
        clear = self.factual.clear('us-sandbox', SANDBOX_UUID, 'latitude,longitude').user('python_driver_tester')
        response = clear.write()
        self.assertIn('commit_id', response)
        self.assertGreater(len(response['commit_id']), 0)

    def test_flag(self):
        flag = self.factual.flag('us-sandbox', SANDBOX_UUID).user('python_driver_tester').other().debug(True)
        response = flag.write()
        self.assertEqual('ok', response['status'])

    def test_flag_preferred(self):
        flag = self.factual.flag('us-sandbox', SANDBOX_UUID).duplicate(FACTUAL_UUID).user('python_driver_tester').debug(True)
        response = flag.write()
        self.assertEqual('ok', response['status'])

    def test_diffs(self):
        diffs = self.factual.diffs('places-us', 1400788800000, 1400792400000).data()
        self.assertGreater(len(diffs), 0)

    def test_diffs_streaming(self):
        diff_request = self.factual.diffs('places-us', 1400788800000, 1400792400000)
        batch = diff_request.data()
        streamed = list(diff_request.stream())
        self.assertSequenceEqual(batch, streamed)

    def test_match(self):
        match = self.factual.match('places-us', {'name':'McDonalds','address':'10451 Santa Monica Blvd','locality':'Los Angeles','region':'CA'})
        match_id = match.get_id()
        self.assertEqual('c730d193-ba4d-4e98-8620-29c672f2f117', match_id)

    def test_multi(self):
        q1 = self.factual.table('places').filters({'postcode':'90067'})
        q2 = self.factual.facets('places').filters({'postcode':'90067'}).select('category_labels')
        response = self.factual.multi({'query1':q1,'query2':q2})
        self.assertTrue('query1' in response and 'query2' in response)

    def test_get_row(self):
        row = self.factual.get_row('places', FACTUAL_UUID)
        self.assertEqual('Factual', row['name'])

    def test_boost(self):
        boost = self.factual.boost('us-sandbox', SANDBOX_UUID).user('python_driver_tester')
        response = boost.write()
        self.assertEqual('ok', response['status'])

if __name__ == '__main__':
    unittest.main()
