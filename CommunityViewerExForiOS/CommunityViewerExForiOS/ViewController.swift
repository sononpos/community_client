//
//  ViewController.swift
//  CommunityViewerExForiOS
//
//  Created by MapleMac on 2017. 5. 8..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit
import SwiftHTTP
import FirebaseCore

class ViewController: UIViewController {
    
    var timer:Timer?
    var cnt = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let versionKey = Bundle.main.infoDictionary![kCFBundleVersionKey as String] as! String
        GVal.BUNDLE_VERSION_KEY = versionKey
        
        if FIRApp.defaultApp() == nil { FIRApp.configure() }
        
        // Do any additional setup after loading the view, typically from a nib.
        HttpHelper.GetCommList(handler: {
            ( bSuccess: Bool , data: Data ) in
            if(bSuccess) {
                do {
                    if let json = try JSONSerialization.jsonObject(with: data, options:[]) as? [String: AnyObject] {
                        //  json parsing
                        for (sign, data) in json {
                            GVal.SetCommInfo(_sKey: sign, _sName: data["name"]!! as! String, _nIdx: data["index"]!! as! Int, _sType: data["viewtype"]!! as! String)
                            let sorted = GVal.GetCommInfoList().sorted { $0.nIndex < $1.nIndex }
                            GVal.aComms = sorted
                        }
                        
                        GVal.LoadFiltered()
                        GVal.LoadRead()
                        
                        DispatchQueue.main.sync {
                            self.timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(self.updateTime), userInfo: nil, repeats: true)
                            /*self.timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true, block: {(timer) in
                                if self.cnt == 0 {
                                    self.cnt = self.cnt + 1
                                    return
                                }
                                
                                self.timer!.invalidate()
                                self.timer = nil
                                
                                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                                let viewController = storyboard.instantiateViewController(withIdentifier :"MainVC")
                                
                                self.present(viewController, animated: true)
                                
                                
                            })*/
                        }
                    }
                    else {
                        print("No Data")
                    }
                }
                catch {
                    print("Could not parse the JSON request")
                }
            }
            else {
                // 오류 팝업
            }
            
        })
    }
    
    override func viewDidAppear(_ animated: Bool) {
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func updateTime() {
        if self.cnt == 0 {
            self.cnt = self.cnt + 1
            return
        }
        
        self.timer!.invalidate()
        self.timer = nil
        
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let viewController = storyboard.instantiateViewController(withIdentifier :"MainVC")
        
        self.present(viewController, animated: true)

    }
}

