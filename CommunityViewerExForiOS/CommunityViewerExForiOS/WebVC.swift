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
    
    var sKey : String?
    var sURL : String?
    var bAppTypeLoad : Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        webView.delegate = self
        if bAppTypeLoad {
            HttpHelper.GetAppTypeText(sKey: sKey!, sURL: sURL!, handler: { (bSuccess, data)->Void in
                do {
                    if let json = try JSONSerialization.jsonObject(with: data, options:[]) as? [AnyObject] {
                        //  json parsings
                        let data = json[0] as? [String : AnyObject]
                        if data != nil {
                            let content = data!["contents"] as! String
                            let title = data!["title"] as! String
                            let finalHtml = "<!DOCTYPE html><html lang=\"ja\"><head><meta charset=\"UTF-8\"><style type=\"text/css\">html{margin:0;padding:0;}body {" +
                            "margin: 0;" +
                            "padding: 0;" +
                            "color: #363636;" +
                            "font-size: 90%;" +
                            "line-height: 1.6;" +
                            "background: #516b82;" +
                            "}" +
                            "img{" +
                            
                            "margin: auto;" +
                            "max-width: 100%;" +
                            
                            "}" +
                            "a{max-width: 100%;}" +
                            "div#title_area{" +
                            "max-width: 100%; padding: 8px; background: #0d065b; color: #fafafa}" +
                            "div#content_area{" +
                            "max-width: 100%; padding: 10px; background: #fafafa; color: #0d065b}" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div id='title_area'><font size=5px>\(title)</font></div>" +
                            "<div id='content_area'>\(content)</div></body>"
                            self.webView.loadHTMLString(finalHtml, baseURL: nil)
                            
                        }
                        
                    }
                    else {
                        print("No Data")
                    }
                }
                catch {
                    print("Could not parse the JSON request")
                }
            })
        }
        else {
            webView.loadRequest(URLRequest(url: URL(string: sURL!)!))
        }
        
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
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        
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

