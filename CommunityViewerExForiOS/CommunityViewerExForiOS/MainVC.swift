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
        tabScrollView.defaultPage = 0
        tabScrollView.tabSectionHeight = 45
        tabScrollView.pagingEnabled = true
        tabScrollView.cachedPageLimit = 2
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
        tabView.frame.size = CGSize(width: 100, height: 45)
        
        let lbTitle = UILabel(frame: tabView.frame)
        lbTitle.text = GVal.GetCommInfoList()[index].sName
        tabView.addSubview(lbTitle)
        
        return tabView
    }
    
    func tabScrollView(_ tabScrollView: ACTabScrollView, contentViewForPageAtIndex index: Int) -> UIView {
        let contentVC = contentViews[index] as! ContentVC
        contentVC.Refresh()
        
        return contentViews[index].view
    }
    
}
