//
//  HttpHelper.swift
//  CommunityViewerExForiOS
//
//  Created by MapleMac on 2017. 5. 11..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import Foundation
import SwiftHTTP

class HttpHelper {
    
    static func GetCommList( handler: @escaping (Bool,Data)->Void )
    {
        do {
            let opt = try HTTP.GET(GVal.URL_COMM_LIST + "?ver=\(GVal.BUNDLE_VERSION_KEY)")
            opt.start {
                response in
                if let err = response.error {
                    print("error : \(err.localizedDescription)")
                    handler(false, Data())
                    return
                }
                
                handler(true, response.data)
            }
        }
        catch let error {
            print("got an error creating the request \(error)")
            handler(false, Data())
        }
    }
    
    static func GetArticleList( sKey: String, nIndex: Int, handler: @escaping (Bool,Data)->Void )
    {
        do {
            let opt = try HTTP.GET(GVal.ARTICLE_URL_BASE + "\(sKey)/\(nIndex)" )
            opt.start {
                response in
                if let err = response.error {
                    print("error : \(err.localizedDescription)")
                    handler(false, Data())
                    return
                }
                
                handler(true, response.data)
            }
        }
        catch let error {
            print("got an error creating the request \(error)")
            handler(false, Data())
        }
    }
    
    static func GetAppTypeText( sKey: String, sURL: String, handler: @escaping (Bool,Data)->Void )
    {
        do {
            let opt = try HTTP.GET(GVal.IPHONE_APP_URL_BASE + "\(sKey)/\(sURL)" )
            opt.start {
                response in
                if let err = response.error {
                    print("error : \(err.localizedDescription)")
                    handler(false, Data())
                    return
                }
                
                handler(true, response.data)
            }
        }
        catch let error {
            print("got an error creating the request \(error)")
            handler(false, Data())
        }
    }
}
