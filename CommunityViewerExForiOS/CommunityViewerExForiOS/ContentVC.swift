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
    var commInfo : CommInfo?
    
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
        return aContents.count
     }
     
     
     // Row display. Implementers should *always* try to reuse cells by setting each cell's reuseIdentifier and querying for available reusable cells with dequeueReusableCellWithIdentifier:
     // Cell gets various attributes set automatically based on table (separators) and data source (accessory views, editing controls)
     
     public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
     {
        let cell: UITableViewCell = UITableViewCell(style: UITableViewCellStyle.subtitle, reuseIdentifier: "Cell")
        cell.textLabel?.text = aContents[indexPath.row].sTitle
        return cell
     }
    
    public func Refresh() {
        LoadArticle()
    }
    
    func LoadArticle() {
        HttpHelper.GetArticleList(sKey: commInfo!.sKey, nIndex: nNextIndex, handler: {
            (bSuccess: Bool, data: Data) in
            do {
                
                if let json = try JSONSerialization.jsonObject(with: data, options:[]) as? [AnyObject] {
                    //  json parsing
                    if let j2 = json[0] as? [String:AnyObject] {
                        var nNextIndex = j2["next_url"] as? String
                        if nNextIndex == nil {
                            nNextIndex = "\((j2["next_url"] as! Int))"
                        }
                        let articles = j2["list"] as! [AnyObject]
                        for data in articles {
                            let newArticle = Content()
                            let data_inner = data as! [String:AnyObject]
                            let sTitle = data_inner["title"]
                            newArticle.sTitle = sTitle as! String
                            self.aContents.append(newArticle)
                        }
                    }
                    DispatchQueue.main.sync {
                        self.tableView.reloadData()
                    }
                    
                    
                }
                else {
                    print("No Data")
                }
            }
            catch {
                print("Could not parse the JSON request")
            }
        } )
    }
}
