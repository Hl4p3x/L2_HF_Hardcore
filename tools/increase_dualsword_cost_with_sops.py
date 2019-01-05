import lxml.etree as ET
import sys

STONE_OF_PURITY_ID = '1875'
ADENA_ID, ANCIENT_ADENA_ID = '57', '5575'

script_name = sys.argv

for file in (
'../l2j_datapack/dist/game/data/multisell/370000000.xml', '../l2j_datapack/dist/game/data/multisell/370000000.xml'):
    dom = ET.parse(file)
    item_elements = dom.findall('.//item')
    change_count = 0
    for element in item_elements:
        ingredients = element.findall('./ingredient')
        for ingredient in ingredients:
            if ingredient.get('id') == STONE_OF_PURITY_ID:
                for additionalIngredient in ingredients:
                    if additionalIngredient.get('id') in (ADENA_ID, ANCIENT_ADENA_ID):
                        additionalIngredient.set('count', str(int(additionalIngredient.get('count')) * 3))
                change_count += 1

    if change_count > 0:
        with open(file, 'wb') as f:
            f.write(ET.tostring(dom))
