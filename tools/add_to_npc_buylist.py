import lxml.etree as ET
import sys

import file_helper as fh

script_name, anchor_id, id_to_add, comment = sys.argv

files_to_process = fh.filter_files_by_suffix(fh.files_to_process("../l2j_datapack/dist/game/data/buylists"), ".xml")

for file_item in files_to_process:
    dom = ET.parse(file_item)

    npcs = dom.findall('.//npc')
    if not npcs:
        print('No npcs were found, skipping file', file_item)
        continue

    items = dom.findall('.//item')

    results = []
    for item in items:
        if item.get('id') == anchor_id:
            print('Adding item', id_to_add, 'to buylist, before', anchor_id)
            element = ET.Element("item")
            element.set('id', id_to_add)
            item.getparent().insert(item.getparent().index(item), element)
            item.getparent().insert(item.getparent().index(item), ET.Comment(comment))
            results.append(element)

    if results:
        with open(file_item, 'wb') as f:
            f.write(ET.tostring(dom, pretty_print=True))
