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
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        do {
            let opt = try HTTP.GET(GVal.URL_COMM_LIST)
            opt.start {
                response in
                if let err = response.error {
                    print("error : \(err.localizedDescription)")
                    return
                }
                
                do {
                    if let json = try JSONSerialization.jsonObject(with: response.data, options:[]) as? [String: AnyObject] {
                        //  json parsing
                        for (sign, data) in json {
                            GVal.SetCommInfo(_sKey: sign, _sName: data["name"]!! as! String, _nIdx: data["index"]!! as! Int)
                        }
                        
                        DispatchQueue.main.sync {
                            let storyboard = UIStoryboard(name: "Main", bundle: nil)
                            let viewController = storyboard.instantiateViewController(withIdentifier :"TestVC")
                            
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
        }
        catch let error {
            print("got an error creating the request \(error)")
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

