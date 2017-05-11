//
//  ViewController.swift
//  CommunityViewerExForiOS
//
//  Created by MapleMac on 2017. 5. 8..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit
import SwiftHTTP

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view, typically from a nib.
        HttpHelper.GetCommList(handler: {
            ( bSuccess: Bool , data: Data ) in
            if(bSuccess) {
                do {
                    if let json = try JSONSerialization.jsonObject(with: data, options:[]) as? [String: AnyObject] {
                        //  json parsing
                        for (sign, data) in json {
                            GVal.SetCommInfo(_sKey: sign, _sName: data["name"]!! as! String, _nIdx: data["index"]!! as! Int)
                            let sorted = GVal.GetCommInfoList().sorted { $0.nIndex < $1.nIndex }
                            GVal.aComms = sorted
                        }
                        
                        DispatchQueue.main.sync {
                            let storyboard = UIStoryboard(name: "Main", bundle: nil)
                            let viewController = storyboard.instantiateViewController(withIdentifier :"MainVC")
                            
                            self.present(viewController, animated: true)
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
}

