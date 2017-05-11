//
//  WebVC.swift
//  CommunityViewerExForiOS
//
//  Created by Wang Yesik on 2017. 5. 11..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit

class WebVC : UIViewController, UIWebViewDelegate {
    
    
    @IBOutlet weak var webView: UIWebView!
    
    var sURL : String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        webView.delegate = self
        webView.loadRequest(URLRequest(url: URL(string: sURL!)!))
        
        let edgePan = UIScreenEdgePanGestureRecognizer(target: self, action: #selector(screenEdgeSwiped))
        edgePan.edges = .left
        
        self.view.addGestureRecognizer(edgePan)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    func screenEdgeSwiped(_ recognizer: UIScreenEdgePanGestureRecognizer ) {
        if recognizer.state == .recognized {
            self.dismiss(animated: true, completion: nil)
        }
    }
}
