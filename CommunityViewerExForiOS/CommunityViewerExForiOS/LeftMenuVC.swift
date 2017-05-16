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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        InitTableView()
        
    }
}

extension LeftMenuVC {
    
    func InitTableView() {
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "LeftMenuCell", for: indexPath)
        cell.textLabel?.text = GVal.GetCommInfoList()[indexPath.row].sName
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath){
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return GVal.GetCommInfoList().count
    }
}

