# -*- coding: UTF8 -*-
import json

config = {
     "buff": [
            {"id": "11334", "name": "强效敏捷", "price": 2, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "11405", "name": "巨人药剂", "price": 3, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "17038", "name": "冬泉火酒", "price": 14, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "17538", "name": "猫鼬药剂", "price": 25, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "17539", "name": "强效奥法药剂", "price": 23, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "17531", "name": "恢复法力", "price": 9, "type": "resources", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "26276", "name": "强效火力", "price": 17, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "27869", "name": "黑暗符文", "price": 48, "type": "resources", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "17544", "name": "防护冰霜", "price": 15, "type": "protection", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "17548", "name": "防护暗影", "price": 36, "type": "protection", "log_type": "SPELL_CAST_SUCCESS",},
            {"id": "11474", "name": "暗影强化", "price": 14, "type": "dps", "log_type": "SPELL_CAST_SUCCESS",},
    ],
}

print(json.dumps(config, indent=2))
