//
//  WebVC.swift
//  CommunityViewerExForiOS
//
//  Created by Wang Yesik on 2017. 5. 11..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit
import GoogleMobileAds

class WebVC : UIViewController, UIWebViewDelegate, GADBannerViewDelegate {
    
    
    @IBOutlet weak var webView: UIWebView!
    @IBOutlet weak var bannerView: GADBannerView!
    
    var sURL : String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        webView.delegate = self
        webView.loadRequest(URLRequest(url: URL(string: sURL!)!))
        
        let edgePan = UIScreenEdgePanGestureRecognizer(target: self, action: #selector(screenEdgeSwiped))
        edgePan.edges = .left
        
        self.view.addGestureRecognizer(edgePan)
        
        bannerView.adUnitID = "ca-app-pub-3598320494828213/8173255886"
        bannerView.delegate = self
        bannerView.rootViewController = self
        bannerView.load(GADRequest())
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    @IBAction func onBtnBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    func screenEdgeSwiped(_ recognizer: UIScreenEdgePanGestureRecognizer ) {
        if recognizer.state == .recognized {
            self.dismiss(animated: true, completion: nil)
        }
    }
}

extension WebVC {
    func adView(_ bannerView: GADBannerView, didFailToReceiveAdWithError error: GADRequestError) {
        print("Banner View Load Failed : \(error)")
    }
    
    func adViewDidReceiveAd(_ bannerView: GADBannerView) {
        print("Banner View Load Succeed")
    }
}

