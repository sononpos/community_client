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
    
    // 커뮤니티 리스트
    static var mComms = [String:CommInfo]()
    static var aComms = [CommInfo]()
    
    static func SetCommInfo(_sKey : String, _sName : String, _nIdx : Int) {
        let newInfo = CommInfo()
        newInfo.sName = _sName
        newInfo.nIndex = _nIdx
        newInfo.sKey = _sKey
        mComms[_sKey] = newInfo
        aComms.append(newInfo)
    }
    
    static func GetCommInfoList() -> [CommInfo] {
        return aComms
    }
}
