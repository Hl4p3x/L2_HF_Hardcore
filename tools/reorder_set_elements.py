import sys
import file_helper as fh
import lxml.etree as ET

script_name = sys.argv

files_to_process = fh.filter_files_by_suffix(fh.files_to_process("../l2j_datapack/dist/game/data/stats/items"), ".xml")

def insert_after(element, new_element):
    parent = element.getparent()
    parent.insert(parent.index(element)+1, new_element)

for file in files_to_process:
    dom = ET.parse(file)
    item_elements = dom.findall('.//item')
    update = 0
    for item in item_elements:
        sets = item.findall('./set')
        last_set = None
        for single_set in sets:
            if single_set.get('name') == 'grade_category' and last_set is not None:
                item.remove(single_set)
                insert_after(last_set, single_set)
                update += 1
            last_set = single_set

    if update > 0:
        with open(file, 'wb') as f:
            f.write(ET.tostring(dom))

