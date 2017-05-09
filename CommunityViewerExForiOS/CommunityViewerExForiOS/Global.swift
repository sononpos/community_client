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
}

class GVal {
    
    static var URL_COMM_LIST : String = "http://4seasonpension.com:3000/list"
    
    // 커뮤니티 리스트
    static var mComms = [String:CommInfo]()
    
    static func SetCommInfo(_sKey : String, _sName : String, _nIdx : Int) {
        let newInfo = CommInfo()
        newInfo.sName = _sName
        newInfo.nIndex = _nIdx
        mComms[_sKey] = newInfo
    }
}
