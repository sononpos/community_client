//
//  MainVC.swift
//  CommunityViewerExForiOS
//
//  Created by Wang Yesik on 2017. 5. 9..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import Foundation
import UIKit
import SwiftHTTP

class MainVC : UIViewController, ACTabScrollViewDelegate, ACTabScrollViewDataSource {
    
    @IBOutlet weak var tabScrollView: ACTabScrollView!
    
    var contentViews = [UIViewController]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initTabView()
        initTabContentViews()
    }

}

extension MainVC {
    
    func initTabView() {
        tabScrollView.defaultPage = 1
        tabScrollView.tabSectionHeight = 60
        tabScrollView.pagingEnabled = true
        tabScrollView.cachedPageLimit = 3
        tabScrollView.delegate = self
        tabScrollView.dataSource = self
    }
    
    func initTabContentViews() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        
        for comm in GVal.GetCommInfoList() {
            let viewController = storyboard.instantiateViewController(withIdentifier :"ContentVC")
            let contentVC = viewController as! ContentVC
            self.addChildViewController(contentVC)
            contentVC.commInfo = comm
            contentViews.append(viewController)
        }
    }
    
    // MARK: ACTabScrollViewDelegate
    func tabScrollView(_ tabScrollView: ACTabScrollView, didChangePageTo index: Int) {
        print(index)
    }
    
    func tabScrollView(_ tabScrollView: ACTabScrollView, didScrollPageTo index: Int) {
    }
    
    // MARK: ACTabScrollViewDataSource
    func numberOfPagesInTabScrollView(_ tabScrollView: ACTabScrollView) -> Int {
        return GVal.GetCommInfoList().count
    }
    
    func tabScrollView(_ tabScrollView: ACTabScrollView, tabViewForPageAtIndex index: Int) -> UIView {
        let tabView = UIView()
        tabView.frame.size = CGSize(width: 100, height: 60)
        
        let lbTitle = UILabel(frame: CGRect(x: 0, y: 0, width: 90, height: 50))
        lbTitle.text = GVal.GetCommInfoList()[index].sName
        tabView.addSubview(lbTitle)
        
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
        
        print("contentViewForPageAtIndex index: \(index)")
        let contentVC = contentViews[index] as! ContentVC
        contentVC.Refresh()
        
        return contentViews[index].view
    }
    
}
