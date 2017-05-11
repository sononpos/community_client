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
    var sViewCnt : String?
    var sUserName : String = ""
    var nCommentCnt : Int = 0
    var sTitle : String = ""
    var sRegDate : String = ""
    var sCommentCnt : String?
}

class ContentVC : UIViewController, UITableViewDelegate, UITableViewDataSource {
    var aContents : [Content]  = [Content]()
    var nNextIndex = 1
    var commInfo : CommInfo?
    
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
        let cell = tableView.dequeueReusableCell(withIdentifier: "ArticleCell", for: indexPath) as! ArticleCell
        
        if aContents.count <= indexPath.row { return cell }
        
        cell.lbTitle.text = aContents[indexPath.row].sTitle
        if cell.lbTitle.text == "" {
            cell.lbTitle.text = "noname"
        }
        cell.lbTitle.sizeToFit()
        cell.lbUserName.text = aContents[indexPath.row].sUserName
        cell.lbUserName.sizeToFit()
        cell.lbRegDate.text = aContents[indexPath.row].sRegDate
        cell.lbRegDate.sizeToFit()
        cell.lbViewCnt.text = aContents[indexPath.row].sViewCnt
        cell.lbViewCnt.sizeToFit()
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let viewController = storyboard.instantiateViewController(withIdentifier :"WebVC") as! WebVC
        viewController.sURL = aContents[indexPath.row].sURL
        self.present(viewController, animated: true)
    }
    
    public func Refresh() {
        self.aContents.removeAll()        
        nNextIndex = 1
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
                            newArticle.sUserName = data_inner["username"] as! String
                            newArticle.sURL = data_inner["link"] as! String
                            newArticle.sRegDate = data_inner["regdate"] as! String
                            newArticle.sViewCnt = data_inner["viewcnt"] as? String
                            if newArticle.sViewCnt == nil {
                                newArticle.sViewCnt = "\(data_inner["viewcnt"] as! Int)"
                            }
                            
                            newArticle.sCommentCnt = data_inner["commentcnt"] as? String
                            if newArticle.sCommentCnt == nil {
                                newArticle.sCommentCnt = "\(data_inner["commentcnt"] as! Int)"
                            }
                            
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

class ArticleCell : UITableViewCell {
    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var lbUserName: UILabel!
    @IBOutlet weak var lbRegDate: UILabel!
    @IBOutlet weak var lbViewCnt: UILabel!
    
}
