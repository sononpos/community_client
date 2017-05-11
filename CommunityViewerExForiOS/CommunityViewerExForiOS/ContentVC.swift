//
//  ContentVC.swift
//  CommunityViewerExForiOS
//
//  Created by MapleMac on 2017. 5. 11..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit

class Content {
    var sURL : String = ""
    var nViewCnt : Int = 0
    var sUserName : String = ""
    var nCommentCnt : Int = 0
    var sTitle : String = ""
}

class ContentVC : UIViewController, UITableViewDelegate, UITableViewDataSource {
    var aContents : [Content]  = [Content]()
    var nNextIndex = 0
    
    var titles = ["Wang" , "Kim" , "Lee"]
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
}

extension ContentVC {
    
     public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
     {
        return titles.count
     }
     
     
     // Row display. Implementers should *always* try to reuse cells by setting each cell's reuseIdentifier and querying for available reusable cells with dequeueReusableCellWithIdentifier:
     // Cell gets various attributes set automatically based on table (separators) and data source (accessory views, editing controls)
     
     public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
     {
        let cell: UITableViewCell = UITableViewCell(style: UITableViewCellStyle.subtitle, reuseIdentifier: "Cell")
        cell.textLabel?.text = titles[indexPath.row]
        return cell
     }
}
