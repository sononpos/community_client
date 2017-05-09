//
//  ViewController.swift
//  CommunityViewerExForiOS
//
//  Created by MapleMac on 2017. 5. 8..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit
import SwiftHTTP

class ViewController: UIViewController, ACTabScrollViewDelegate, ACTabScrollViewDataSource {
    
    @IBOutlet weak var tabScrollView: ACTabScrollView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view, typically from a nib.
        tabScrollView.defaultPage = 1
        tabScrollView.tabSectionHeight = 60
        tabScrollView.pagingEnabled = true
        tabScrollView.cachedPageLimit = 3
        
        tabScrollView.dataSource = self
        tabScrollView.delegate = self
        
        do {
            let opt = try HTTP.GET(GVal.URL_COMM_LIST)
            opt.start {
                response in
                if let err = response.error {
                    print("error : \(err.localizedDescription)")
                    return
                }
                
                print("opt finished: \(response.description)")
                
                do {
                    if let json = try JSONSerialization.jsonObject(with: response.data, options:[]) as? [String: AnyObject] {
                        //  json parsing
                        print("json data count : \(json.count)")
                        for (sign, data) in json {
                            print("Sign : \(sign), Data : \(data["index"]!!)")
                            GVal.SetCommInfo(_sKey: sign, _sName: data["name"]!! as! String, _nIdx: data["index"]!! as! Int)
                        }
                        
                        let storyboard = UIStoryboard(name: "Main", bundle: nil)
                        let viewController = storyboard.instantiateViewController(withIdentifier :"MainVC") as! MainVC
                        self.present(viewController, animated: true)
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
    
    // MARK: ACTabScrollViewDelegate
    func tabScrollView(_ tabScrollView: ACTabScrollView, didChangePageTo index: Int) {
        print(index)
    }
    
    func tabScrollView(_ tabScrollView: ACTabScrollView, didScrollPageTo index: Int) {
    }
    
    // MARK: ACTabScrollViewDataSource
    func numberOfPagesInTabScrollView(_ tabScrollView: ACTabScrollView) -> Int {
        return 8
    }
    
    func tabScrollView(_ tabScrollView: ACTabScrollView, tabViewForPageAtIndex index: Int) -> UIView {
        let tabView = UIView()
        tabView.frame.size = CGSize(width: (index + 1) * 10, height: (index + 1) * 5)
        
        switch (index % 3) {
        case 0:
            tabView.backgroundColor = UIColor.red
        case 1:
            tabView.backgroundColor = UIColor.green
        case 2:
            tabView.backgroundColor = UIColor.blue
        default:
            break
        }
        
        return tabView
    }
    
    func tabScrollView(_ tabScrollView: ACTabScrollView, contentViewForPageAtIndex index: Int) -> UIView {
        let contentView = UIView()
        
        switch (index % 3) {
        case 0:
            contentView.backgroundColor = UIColor.red
        case 1:
            contentView.backgroundColor = UIColor.green
        case 2:
            contentView.backgroundColor = UIColor.blue
        default:
            break
        }
        
        return contentView
    }


}

