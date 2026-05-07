import unittest

# Red test v2 for scenario: Commande simple avec un article disponible
# Improved naming and structure following prompt conventions

class TestPlaceOrderSimpleV2(unittest.TestCase):
    def test_shouldCreateOrderWhenItemAvailable(self):
        # Given: a festival goer and one available item
        festival_goer_id = "fg-1"
        item = {"id": "item-1", "type": "drink", "available": True, "cost_drink": 1}

        # When: attempting to place an order
        try:
            from domain.order import place_order
            order = place_order(festival_goer_id, [item])
            # Then: the order should be created with status 'CREATED'
            self.assertIsNotNone(order)
            self.assertEqual(getattr(order, 'status', None), 'CREATED')
        except Exception:
            # Fail explicitly to ensure this Red test remains failing until implementation
            self.fail('place_order is not implemented or raised an unexpected error')

if __name__ == '__main__':
    unittest.main()
