import sys
import file_helper as fh
import lxml.etree as ET

script_name, attribute, contains_text = sys.argv

files_to_process = fh.filter_files_by_suffix(fh.files_to_process('../l2j_datapack/dist/game/data/stats/items/'), '.xml')

item_ids = []
for item in files_to_process:
    dom = ET.parse(item)
    item_elements = dom.findall('.//item')
    change_count = 0
    for element in item_elements:
        if contains_text.lower() in element.get(attribute).lower():
            print('Item', element.get(attribute),
                  '[', element.get('id'), ']')

            for set_item in element.findall('./set'):
                if set_item.get('name') in ['is_tradable', 'is_dropable', 'is_sellable']:
                    set_item.set('val', 'true')
                    change_count += 1

    if change_count > 0:
        with open(item, 'wb') as f:
            f.write(ET.tostring(dom))
