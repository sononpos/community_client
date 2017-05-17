//
//  LeftMenuVC.swift
//  CommunityViewerExForiOS
//
//  Created by Wang Yesik on 2017. 5. 17..
//  Copyright © 2017년 MapleMac. All rights reserved.
//

import UIKit

class LeftMenuVC : UIViewController, UITableViewDelegate, UITableViewDataSource {
    @IBOutlet weak var tableView: UITableView!
    
    var filteredLisner: ()->Void = {}
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        InitTableView()
        
    }
    
    func setFilteredListener(handler: @escaping ()->Void) {
        filteredLisner = handler
    }
}

extension LeftMenuVC {
    
    func InitTableView() {
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "LeftMenuCell", for: indexPath)
        let content = GVal.GetCommInfoListNotFiltered()[indexPath.row]
        cell.textLabel?.text = content.sName
        
        if GVal.IsFiltered(s: content.sKey) {
            cell.textLabel?.textColor = UIColor(colorLiteralRed: 205/255, green: 205/255, blue: 205/255, alpha: 1)
        }
        else {
            cell.textLabel?.textColor = UIColor.black
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath){
        if GVal.IsFiltered(s: GVal.GetCommInfoListNotFiltered()[indexPath.row].sKey) {
            GVal.RemoveFiltered(s: GVal.GetCommInfoListNotFiltered()[indexPath.row].sKey)
        }
        else {
            GVal.SetFiltered(s: GVal.GetCommInfoListNotFiltered()[indexPath.row].sKey)
        }
        tableView.reloadData()
        filteredLisner()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return GVal.GetCommInfoListNotFiltered().count
    }
}

