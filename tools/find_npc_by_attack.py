import sys
import file_helper as fh
import lxml.etree as ET
import attack_normalizer as an

def breaks_treshold(actual, threshold):
    return actual is not None and float(actual) >= float(threshold)


def print_treshold_break(attribute, npc, actual, threshold):
    print('{} [{}] has broken treshold of {} [{}] with value of {}'.format(npc.get('name'), npc.get('id'), attribute, threshold, actual))


script_name, p_attack_treshold, m_attack_treshold, hp_treshold, mp_treshold = sys.argv

workdir = '../l2j_datapack/dist/game/data/stats/npcs'

files_to_process = fh.filter_files_by_suffix(fh.files_to_process(workdir), '.xml')


for item in files_to_process:
    magical_breaks = set()
    physical_breaks = set()
    dom = ET.parse(item)
    for npc in dom.findall('.//npc'):
        if npc.get('type') != 'L2Monster' or npc.get('title') == "Raid Fighter":
            break

        stats = npc.find('stats')
        if stats is not None:
            attack = stats.find('attack')
            if attack is not None:
                p_attack = attack.get('physical')
                if breaks_treshold(p_attack, p_attack_treshold):
                    print_treshold_break('physical', npc, p_attack, p_attack_treshold)
                    physical_breaks.add(float(p_attack))
                    #attack.set("physical", str(an.normalize_p_attack(float(p_attack))))

                m_attack = attack.get('magical')
                if breaks_treshold(m_attack, m_attack_treshold):
                    print_treshold_break('magical', npc, m_attack, m_attack_treshold)
                    magical_breaks.add(float(m_attack))
                    #attack.set("magical", str(an.normalize_m_attack(float(m_attack))))

            vitals = stats.find('vitals')
            if vitals is not None:
                hp = attack.get('hp')
                if breaks_treshold(hp, hp_treshold):
                    print_treshold_break('hp', npc, hp, hp_treshold)

                mp = attack.get('mp')
                if breaks_treshold(mp, mp_treshold):
                    print_treshold_break('mp', npc, mp, mp_treshold)

    #if magical_breaks or physical_breaks:
    with open(item, 'wb') as f:
        print("Overriding file", item)
        f.write(ET.tostring(dom))
