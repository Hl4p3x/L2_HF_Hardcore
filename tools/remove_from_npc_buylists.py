import lxml.etree as ET
import os
import sys

import file_helper as fh

script_name, workdir, ids_file = sys.argv

ids_to_remove = fh.parse_ids_file(ids_file)
files_to_process = fh.filter_files_by_suffix(fh.files_to_process("../l2j_datapack/dist/game/data/buylists"), ".xml")

print('Ids to remove:', ids_to_remove)

for file_item in files_to_process:
    dom = ET.parse(file_item)

    npcs = dom.findall('.//npc')
    if not npcs:
        print('No npcs were found, skipping file', file_item)
        continue

    items = dom.findall('.//item')

    was_empty = False
    if not items:
        was_empty = True
    results = []
    for item in items:
        if item.get('id') in ids_to_remove and item.getparent() and not item.get('price'):
            print('Removing item', item.get('id'), 'from buylist')
            item.getparent().remove(item)
            results.append(item)

    new_items = dom.findall('.//item')
    if not new_items and not was_empty:
        print('No items left in', file_item)
        os.remove(file_item)
    elif results:
        with open(file_item, 'wb') as f:
            f.write(ET.tostring(dom))
