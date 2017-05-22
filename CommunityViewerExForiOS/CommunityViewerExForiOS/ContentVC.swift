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
    var sTitle : String = ""
    var sRegDate : String = ""
    var sCommentCnt : String?
    var sLinkEncoding : String?
}

class ContentVC : UIViewController, UITableViewDelegate, UITableViewDataSource {
    var aContents : [Content]  = [Content]()
    var nNextIndex = 1
    var bLoadingData = false
    var commInfo : CommInfo?
    
    var alertController : UIAlertController?
    
    @IBOutlet weak var tableView: UITableView!
    let refreshControl = UIRefreshControl()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupTableView()
        
        
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
    }
}

extension ContentVC {
    
    func setupTableView() {
        if #available(iOS 10.0, *) {
            tableView.refreshControl = self.refreshControl
        }
        else {
            tableView.addSubview(refreshControl)
        }
        
        refreshControl.addTarget(self, action: #selector(Refresh), for: .valueChanged)
        refreshControl.attributedTitle = NSAttributedString(string: "Refresh List...")
        
        tableView.delegate = self
        tableView.dataSource = self
    }
    
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
        let content = aContents[indexPath.row]
        
        let sTitle2 = "\(content.sTitle)..[100]"
        let sTitle = sTitle2 as NSString
        var sFinalTitle = content.sTitle
        
        let size = sTitle.size(attributes: [NSFontAttributeName: UIFont.systemFont(ofSize: 17.0)])
        if cell.lbTitle.frame.width < size.width {
            let gap = Int((size.width - cell.lbTitle.frame.width + 150) / 17.0)
            let idx = sFinalTitle.index(sFinalTitle.startIndex, offsetBy: sFinalTitle.characters.count - gap )
            sFinalTitle = "\(sFinalTitle.substring(to: idx)).."
        }
        
        let titleColor = GVal.IsRead(s: content.sTitle.hash) ? "#bfbfbf" : "#000000"
        let htmlTitle = "<font color='\(titleColor)'>\(sFinalTitle)</font> <font color=red>[\(content.sCommentCnt!)]</font>"
        do {
            cell.lbTitle.attributedText = try NSAttributedString(data: htmlTitle.data(using: String.Encoding.unicode, allowLossyConversion: true)!, options: [NSDocumentTypeDocumentAttribute:NSHTMLTextDocumentType], documentAttributes: nil)
            cell.lbTitle.font = UIFont(name: "System", size: 17)
        }
        catch let error {
            print(error)
        }
        
        
        if cell.lbTitle.text == nil || cell.lbTitle.text!.isEmpty {
            cell.lbTitle.text = "NoTitle"
        }
        cell.lbTitle.sizeToFit()
        
        
        cell.lbUserName.text = content.sUserName
        if cell.lbUserName.text == nil || cell.lbUserName.text!.isEmpty {
            cell.lbUserName.text = "NoName"
        }
        cell.lbUserName.sizeToFit()
        
        
        cell.lbRegDate.text = content.sRegDate
        if cell.lbRegDate.text == nil || cell.lbRegDate.text!.isEmpty {
            cell.lbRegDate.text = "No Date"
        }
        cell.lbRegDate.sizeToFit()
        
        
        cell.lbViewCnt.text = content.sViewCnt
        if cell.lbViewCnt.text == nil || cell.lbViewCnt.text!.isEmpty {
            cell.lbViewCnt.text = "-"
        }
        cell.lbViewCnt.sizeToFit()
        return cell
    }
    
    // 글 선택
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let viewController = storyboard.instantiateViewController(withIdentifier :"WebVC") as! WebVC
        if aContents.count <= indexPath.row { return }
        let content = aContents[indexPath.row]
        GVal.SetRead(s: content.sTitle.hash)
        if commInfo!.sType == "app" {
            viewController.sURL = content.sLinkEncoding
            viewController.sKey = commInfo!.sKey
            viewController.bAppTypeLoad = true
        }
        else {
            viewController.sURL = content.sURL
        }
        tableView.reloadRows(at: [indexPath], with: UITableViewRowAnimation.none)
        self.present(viewController, animated: true)
    }
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if !bLoadingData && indexPath.row == aContents.count - 1 && nNextIndex > 1 {
            LoadArticle()
        }
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
                if( bSuccess ) {
                    if let json = try JSONSerialization.jsonObject(with: data, options:[]) as? [AnyObject] {
                        //  json parsing
                        if let j2 = json[0] as? [String:AnyObject] {
                            var nNext = j2["next_url"] as? String
                            if nNext == nil {
                                nNext = "\((j2["next_url"] as! Int))"
                            }
                            self.nNextIndex = Int(nNext!)!
                            let articles = j2["list"] as! [AnyObject]
                            for data in articles {
                                let newArticle = Content()
                                let data_inner = data as! [String:AnyObject]
                                let sTitle = data_inner["title"]
                                newArticle.sTitle = sTitle as! String
                                newArticle.sUserName = data_inner["username"] as! String
                                newArticle.sURL = data_inner["link"] as! String
                                newArticle.sLinkEncoding = data_inner["linkencoding"] as? String
                                newArticle.sRegDate = data_inner["regdate"] as! String
                                newArticle.sViewCnt = data_inner["viewcnt"] as? String
                                if newArticle.sViewCnt == nil {
                                    newArticle.sViewCnt = "0"
                                }
                                
                                newArticle.sCommentCnt = data_inner["commentcnt"] as? String
                                if newArticle.sCommentCnt == nil || newArticle.sCommentCnt!.isEmpty {
                                    newArticle.sCommentCnt = "0"
                                }
                                
                                self.aContents.append(newArticle)
                            }
                        }
                        DispatchQueue.main.sync {
                            self.bLoadingData = false
                            self.tableView.reloadData()
                            self.refreshControl.endRefreshing()
                        }
                        
                        
                    }
                    else {
                        print("No Data")
                        DispatchQueue.main.sync {
                            self.bLoadingData = false
                            self.refreshControl.endRefreshing()
                        }
                    }
                }
                else {
                    DispatchQueue.main.sync {
                        self.bLoadingData = false
                        self.refreshControl.endRefreshing()
                    }
                }
                
            }
            catch {
                print("Could not parse the JSON request")
                DispatchQueue.main.sync {
                    self.bLoadingData = false
                    self.refreshControl.endRefreshing()
                }
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
