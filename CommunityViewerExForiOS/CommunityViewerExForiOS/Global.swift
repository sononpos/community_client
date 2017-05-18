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
    static var KEY_READ_LIST = "READ_LIST"
    
    static var filtered : NSMutableSet = NSMutableSet()
    
    // 커뮤니티 리스트
    static var mComms = [String:CommInfo]()
    static var aComms = [CommInfo]()
    static var aCommsFiltered = [CommInfo]()
    
    // 읽었던 글
    static var readArticle : NSMutableSet = NSMutableSet()
    
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
    
    static func LoadRead() {
        let arr = UserDefaults.standard.array(forKey: KEY_READ_LIST) as? [Int]
        if(arr != nil) {
            readArticle.removeAllObjects()
            for s in arr! {
                readArticle.add(s)
            }
        }
    }
    
    static func SetRead(s:Int) {
        if readArticle.contains(s) {return}
        if readArticle.count > 300 {
            for d in readArticle {
                readArticle.remove(d)
                break
            }
        }
        readArticle.add(s)
        
        UserDefaults.standard.set(readArticle.allObjects, forKey: KEY_READ_LIST)
    }
    
    static func RemoveRead(s:Int) {
        if !readArticle.contains(s) {return}
        readArticle.remove(s)
        UserDefaults.standard.set(readArticle.allObjects, forKey: KEY_READ_LIST)
    }
    
    static func IsRead(s:Int) -> Bool {
        return readArticle.contains(s)
    }
}
