import unittest

# Red test for scenario: Commande simple avec un article disponible
# This test exercises the domain place order behavior and is expected to fail
# because the production implementation does not exist yet.

class TestPlaceOrderSimple(unittest.TestCase):
    def test_place_simple_order_item_available(self):
        # Given: a festival goer and one available item
        festival_goer_id = "fg-1"
        item = {"id": "item-1", "type": "drink", "available": True, "cost_drink": 1}

        # When: placing an order for the item
        # NOTE: production code should implement `place_order` in the domain layer.
        try:
            from domain.order import place_order  # expected to be missing initially
            order = place_order(festival_goer_id, [item])
            # Then: an order is created with status CREATED
            self.assertIsNotNone(order)
            self.assertEqual(getattr(order, 'status', None), 'CREATED')
        except Exception:
            # Make the test fail explicitly if implementation is absent or raises
            self.fail('place_order is not implemented or raised an unexpected error')

if __name__ == '__main__':
    unittest.main()
