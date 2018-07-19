def normalize_m_attack(value):
    if value < 2000:
        return value
    if value < 3000:
        return value / 2.15
    if value < 4000:
        return value / 2.3
    if value < 5000:
        return value / 2.3
    if value < 6000:
        return value / 2.30
    if value < 7000:
        return value / 2.30
    if value < 8000:
        return value / 2.30
    if value < 9000:
        return value / 2.30
    else:
        return value / 2.3


def normalize_p_attack(value):
    if value < 2000:
        return value
    if value < 5000:
        return value / 2.2
    if value < 6000:
        return value / 2.4
    if value < 7000:
        return value / 2.5
    if value < 8000:
        return value / 2.35
    if value < 8800:
        return value / 2.35
    if value < 10000:
        return value / 2.4
    if value < 12000:
        return value / 2.5
    else:
        return value / 2.6
