//
//  Global.swift
//  CommunityViewerExForiOS
//
//  Created by Wang Yesik on 2017. 5. 9..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import Foundation

//  커뮤니티 리스트 정보
class CommInfo {
    var sName : String = ""
    var nIndex : Int = 0
    var sKey : String = ""
}

class GVal {
    
    static var ARTICLE_URL_BASE : String = "http://4seasonpension.com:3000/";
    static var URL_COMM_LIST : String = "http://4seasonpension.com:3000/list"
    static var KEY_FILTERED_LIST = "FILTERED_LIST"
    
    static var filtered : NSMutableSet = NSMutableSet()
    
    // 커뮤니티 리스트
    static var mComms = [String:CommInfo]()
    static var aComms = [CommInfo]()
    static var aCommsFiltered = [CommInfo]()
    
    static func SetCommInfo(_sKey : String, _sName : String, _nIdx : Int) {
        let newInfo = CommInfo()
        newInfo.sName = _sName
        newInfo.nIndex = _nIdx
        newInfo.sKey = _sKey
        mComms[_sKey] = newInfo
        aComms.append(newInfo)
    }
    
    static func GetCommInfoList() -> [CommInfo] {
        if filtered.count > 0 {
            return aCommsFiltered
        }
        return aComms
    }
    
    static func GetCommInfoListNotFiltered() -> [CommInfo] {
        return aComms
    }
    
    static func LoadFiltered() {
        let arr = UserDefaults.standard.stringArray(forKey: KEY_FILTERED_LIST)
        if(arr != nil) {
            filtered.removeAllObjects()
            for s in arr! {
                filtered.add(s)
            }
        }
        
        aCommsFiltered.removeAll()
        for info in aComms {
            if !filtered.contains(info.sKey) {
                aCommsFiltered.append(info)
            }
        }
    }
    
    static func SetFiltered(s:String) {
        if filtered.contains(s) {return}
        filtered.add(s)
        UserDefaults.standard.set(filtered.allObjects, forKey: KEY_FILTERED_LIST)
        
        aCommsFiltered.removeAll()
        for info in aComms {
            if !filtered.contains(info.sKey) {
                aCommsFiltered.append(info)
            }
        }
    }
    
    static func RemoveFiltered(s:String) {
        if !filtered.contains(s) {return}
        filtered.remove(s)
        UserDefaults.standard.set(filtered.allObjects, forKey: KEY_FILTERED_LIST)
        
        aCommsFiltered.removeAll()
        for info in aComms {
            if !filtered.contains(info.sKey) {
                aCommsFiltered.append(info)
            }
        }
    }
    
    static func IsFiltered(s:String) -> Bool {
        return filtered.contains(s)
    }
}
