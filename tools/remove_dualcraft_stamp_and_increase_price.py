import lxml.etree as ET
import sys

import file_helper as fh

DUALCRAFT_STAMP_ID = '5126'
ADENA_ID, ANCIENT_ADENA_ID = '57', '5575'

script_name = sys.argv

files_to_process = fh.filter_files_by_suffix(fh.files_to_process('../l2j_datapack/dist/game/data/multisell/'), '.xml')

for file in files_to_process:
    dom = ET.parse(file)
    item_elements = dom.findall('.//item')
    change_count = 0
    for element in item_elements:
        ingredients = element.findall('./ingredient')
        for ingredient in ingredients:
            if ingredient.get('id') == DUALCRAFT_STAMP_ID:
                comment_sibling = ingredient.getprevious()
                if comment_sibling is not None and comment_sibling.tag is ET.Comment:
                    element.remove(comment_sibling)
                element.remove(ingredient)

                for additionalIngredient in ingredients:
                    if additionalIngredient.get('id') in (ADENA_ID, ANCIENT_ADENA_ID):
                        additionalIngredient.set('count', str(int(additionalIngredient.get('count')) * 3))
                change_count += 1

    if change_count > 0:
        with open(file, 'wb') as f:
            f.write(ET.tostring(dom))
