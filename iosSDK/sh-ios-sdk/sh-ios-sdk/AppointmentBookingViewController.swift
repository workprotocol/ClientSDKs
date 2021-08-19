//
//  AppointmentBookingViewController.swift
//  sdk
//
//  Created by Riya Balhara on 30/07/21.
//

import UIKit
import Foundation

class AppointmentBookingViewController: UIViewController {
    @IBOutlet weak var servicePrice: UILabel!
    
    @IBOutlet weak var reasonLineView: UIView!
    @IBOutlet weak var serviceName: UILabel!
    
    @IBOutlet weak var navigationLabel: UILabel!
    
    @IBOutlet weak var dropDownPickIssue: UIButton!
    @IBOutlet weak var mainScrollBar: UIScrollView!
    @IBOutlet weak var navigationRightSpace: UILabel!
    @IBOutlet weak var navigationBackButton: UIButton!
    let transparentView = UIView()
    let tableView = UITableView()
    var selectedButton = UIButton()
    var dataSource = [String]()
    
    
    let buttonPadding:CGFloat = 20
    var specialistId:String?
    var timeSlotsArr : [[String : String]] = [[ : ]]
    var specialistName:String?
    var serviceId:String?
    var vendorId:String?
    var serviceNameStr:String?
    var servicePriceInStr:String?
    var speciality:String?
    var infoArr:[String : String]?
    public init() {
        super.init(nibName: "AppointmentBookingViewController", bundle: Bundle(for: AppointmentBookingViewController.self))
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    public init(specialistArr : [String : String]) {
        specialistId = specialistArr["id"]
        specialistName = specialistArr["name"]
        serviceId = specialistArr["serviceId"]
        vendorId = specialistArr["vendorId"]
        speciality = specialistArr["speciality"]
        infoArr = specialistArr
        super.init(nibName: "AppointmentBookingViewController", bundle: Bundle(for: AppointmentBookingViewController.self))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(CellClass.self, forCellReuseIdentifier: "Cell")
        if(SpriteHealthClient.primaryColor != "")
        {
            let primaryColor = SpriteHealthClient.hexStringToUIColor(hex:SpriteHealthClient.primaryColor)
            let primaryTextColor = SpriteHealthClient.hexStringToUIColor(hex:SpriteHealthClient.primaryTextColor)
            navigationLabel.backgroundColor = primaryColor
            navigationLabel.textColor = primaryTextColor
            navigationBackButton.backgroundColor = primaryColor
            navigationBackButton.tintColor = primaryTextColor
            
            navigationRightSpace.backgroundColor = primaryColor
            navigationRightSpace.textColor = primaryTextColor
            reasonLineView.backgroundColor = primaryColor
            dropDownPickIssue.tintColor = primaryColor
        }
        let homeImage = UIImage(systemName: "chevron.down")
        
        dropDownPickIssue.setTitle("Pick Issue", for: .normal)
        dropDownPickIssue.setTitleColor(.gray, for: .normal)
        dropDownPickIssue.setImage(homeImage, for: .normal)
        dropDownPickIssue.contentHorizontalAlignment = .left
       
        view.isUserInteractionEnabled = false
        let myActivityIndicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                       
                       // Position Activity Indicator in the center of the main view
                       myActivityIndicator.center = view.center
               
                       // If needed, you can prevent Acivity Indicator from hiding when stopAnimating() is called
                       myActivityIndicator.hidesWhenStopped = true
                       
                       // Start Activity Indicator
                       myActivityIndicator.startAnimating()
               view.addSubview(myActivityIndicator)
        
        let cmObj = SpriteHealthClient()
        cmObj.specialistAvailableSlot(specialistId: String(specialistId!), serviceId: String(serviceId!)){ (error, result) in
            if let error = error {
                print(error)
            } else {
            // do stuff with the result
            let resultConverted = result.replacingOccurrences(of: "\\", with: "")
            if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {

               print(json);
                print(type(of: json))
                DispatchQueue.main.async {
                self.displayDetails(json: json)
                   // myActivityIndicator.stopAnimating()
                }
            }
            }
        }
        
        
        if((serviceId != "") && (vendorId != ""))
        {
            cmObj.getServicePricing(serviceId: serviceId!, vendorId: vendorId! ){ (error, result) in
                if let error = error {
                    print(error)
                } else {
                // do stuff with the result
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [AnyObject] {

                   print(json);
                    print(type(of: json))
                    DispatchQueue.main.async { [self] in
                        
                        
                        if let wpName = json[0]["wpName"] as? String {
                            self.serviceName.text = wpName
                            serviceNameStr = wpName
                            
                        }
                       
                        var vendorId = 0;
                        var serviceCode = "";
                        if let wpVendorId = json[0]["wpVendorId"] as? Int {
                            vendorId = wpVendorId
                            
                        }
                        if let wpCode = json[0]["wpCode"] as? String {
                            serviceCode = wpCode
                            
                        }
                        let requestParameters: [String: Any] = [
                            "memberId": (infoArr?["memberId"])! as String,
                            "organizationId": (infoArr?["organizationId"])! as String,
                             "providerId": vendorId,
                             "serviceCode": serviceCode,
                             "termType": "Purchase",
                            "specialistId": specialistId! as String,
                            "specialityNames": speciality! as String,
                             "operation": "COMPUTE",
                             "units": 1

                            ]
                        print(requestParameters)
                        cmObj.fetchServiceCoverage(requestParameters: requestParameters)
                        { (error, result) in
                            if let error = error {
                                print(error)
                                myActivityIndicator.stopAnimating()
                            } else {
                                // do stuff with the result
                                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                                if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String:Any] {

                                   print(json);
                                    print(type(of: json))
                                    
                                    DispatchQueue.main.async { [self] in
                                        
                                        if let groupTotalPatientAmount = json["groupedTotalPatientAmount"] as? NSDictionary {
                                            if let insured = groupTotalPatientAmount["Insured"] as? Float32 {
                                                
                                                self.servicePrice.text = "$" + String(insured)
                                                servicePriceInStr = "$" + String(insured)
                                            }
                                            
                                            
                                        }
                                        myActivityIndicator.stopAnimating()
                                        self.view.isUserInteractionEnabled = true
                                       
                                    }
                                    
                                    
                                }
                            }
                        }
                        
                        
                        
                        
                    }
                }
                }
            }
        }
        
      
        cmObj.fetchReasonsBySpecialities(specialities: "26"){ (error, result) in
            // do stuff with the result
            if let error = error {
                print(error)
            } else {
            let resultConverted = result.replacingOccurrences(of: "\\", with: "")
            if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? AnyObject? {

                print(json as Any);
                print(type(of: json))
                DispatchQueue.main.async {
                self.pushReasons(json: json)
                }
            }
            }
        }
        
        
     
    }
    
    func pushReasons(json:AnyObject?) {
        print(json as Any)
        if let reasons = json as? [NSDictionary] {
            for reason in reasons
            {
                dataSource.append(reason["name"] as! String)
            }
        }
    }
    func displayDetails(json: [String: Any]) {
        print(json)
        
        var count = 0;
        if let freeTimePeriods = json["freeTimePeriods"] as? [NSDictionary] {
            
            for freeTimePeriod in freeTimePeriods
            {
                
                let startTime =  freeTimePeriod["startTime"] as! String
                let endTime = freeTimePeriod["endTime"] as! String
                print(startTime)
                print(endTime)
                
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "MM-dd-yyyy HH:mm:ss"
                let date = dateFormatter.date(from: startTime)
                dateFormatter.dateFormat = "EEEE, MMM d##h:mm a"
                let startTimeAfterMan = dateFormatter.string(from: date!)
                dateFormatter.dateFormat = "MM-dd-yyyy HH:mm:ss"
                let date2 = dateFormatter.date(from: endTime)
                dateFormatter.dateFormat = "EEEE, MMM d##h:mm a"
                let endTimeAfterMan = dateFormatter.string(from: date2!)
                
                count = count + 1
                self.timeSlotsArr.append([
                    "id": String(count),
                    "startTime": startTimeAfterMan,
                    "endTime": endTimeAfterMan,
                    "specialistId" : specialistId!,
                    "specialistName" :specialistName!,
                    "startTimeRaw" : startTime,
                    "endTimeRaw": endTime
                ]);
                
               
            //genderLabel.text = gender
            }
        }
        
        createTimeSlots()
    }
    func createTimeSlots()
    {
        
        print(self.timeSlotsArr)
        var currentDate = ""
        let xTopLeft = 0
        var yTopLeft = 215 + 20
        let width = mainScrollBar.frame.size.width
        let height = 25
        let xOffset:CGFloat = 20
        var createSepArray :[[String:String]] = []
        var count = -1;
        for timeslot in self.timeSlotsArr
        {
            if(count == -1){
                count = 0
                continue;
            }
            //let id  = timeslot["id"]
            let startTime = timeslot["startTime"]
            let startTimeSrr = startTime!.components(separatedBy: "##")
            let startTimeFirstPart    = startTimeSrr[0]
            //let startTimeSecondPart = startTimeSrr[1]
            
            if(currentDate == startTimeFirstPart)
            {
                createSepArray.append(timeslot)
                print(createSepArray)
            }
            else if(!createSepArray.isEmpty)
            {
                
                
                let  label = UILabel(frame: CGRect(x: xTopLeft+10, y: yTopLeft, width: Int(width), height: height))
                mainScrollBar.addSubview(label)
                createLabel(label: label, textColor: .black, textSize: 22, text: currentDate)
                
                let scView = UIScrollView(frame: CGRect(x: 0, y: yTopLeft + 25, width: Int(mainScrollBar.frame.size.width), height: 70))
                mainScrollBar.addSubview(scView)
                addHorizontalScorebar(scView: scView,xOffset:xOffset, btnArray: createSepArray )
                createSepArray  = []
                yTopLeft = yTopLeft + 120
                currentDate = startTimeFirstPart
            }
            else{
                currentDate = startTimeFirstPart
                createSepArray.append(timeslot)
            }
            
        }
        mainScrollBar.contentSize = CGSize(width: Int(mainScrollBar.frame.size.width), height: yTopLeft)
    }
    @objc func scrollButtonAction(sender: UIButton) {
            print("Hello \(sender.tag) is Selected")
        print(timeSlotsArr[sender.tag ])
        //print(specialistArr[sender.tag]["name"])
        var specialistArr = timeSlotsArr[sender.tag]
        
        specialistArr["serviceName"] = serviceNameStr!
        specialistArr["servicePrice"] = servicePriceInStr!
        specialistArr["speciality"] = speciality!
        
        specialistArr["memberEmail"] = infoArr?["memberEmail"]
        specialistArr["memberName"] = infoArr?["memberName"]
        specialistArr["memberId"] = infoArr?["memberId"]
        specialistArr["memberPhone"] = infoArr?["memberPhone"]
        specialistArr["organizationId"] = infoArr?["organizationId"]
       
       let newViewController = ReviewAppointmentViewController(specialistArr: specialistArr)
        newViewController.modalPresentationStyle = .fullScreen
        self.present(newViewController, animated:true,completion:nil)
        }
    
    func addHorizontalScorebar(scView: UIScrollView,xOffset:CGFloat,btnArray:[[String: String]])
    {
        scView.backgroundColor = UIColor.white
        scView.translatesAutoresizingMaskIntoConstraints = false
        var xOffsetLocal = xOffset
        for btn in btnArray {
            let startTimeSrr = btn["startTime"]!.components(separatedBy: "##")
            
            
            let button = UIButton()
            button.tag = (Int(btn["id"]!))!
            button.backgroundColor = UIColor.white
            
            button.setTitle(startTimeSrr[1], for: .normal)
            button.addTarget(self, action: #selector(self.scrollButtonAction), for: .touchUpInside)

            button.frame = CGRect(x: xOffsetLocal, y: CGFloat(buttonPadding), width: 100, height: 40)
          
            button.layer.cornerRadius = 5
            button.layer.borderWidth = 1
            button.layer.borderColor = UIColor.black.cgColor
            button.setTitleColor(.black, for: .normal)
            xOffsetLocal = xOffsetLocal + CGFloat(buttonPadding) + button.frame.size.width
            scView.addSubview(button)


        }

        scView.contentSize = CGSize(width: xOffsetLocal, height: scView.frame.height)
    }
    
    
    func createLabel(label:UILabel, textColor: UIColor, textSize : CGFloat, text: String)
          {
               
               label.textColor = textColor
               label.font = label.font.withSize(textSize)
               label.text  = text
               label.textAlignment = .left
              
               
          }
   
    
    @IBAction func navigationBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
        
    }
   
    @IBAction func clickListener_PickIssue(_ sender: Any) {
        
            selectedButton = dropDownPickIssue
            addTransparentView(frames: dropDownPickIssue.frame)
        
    }
    func addTransparentView(frames: CGRect) {
             //let window = UIApplication.shared.keyWindow
            let window = UIApplication.shared.connectedScenes
                .filter({$0.activationState == .foregroundActive})
                .map({$0 as? UIWindowScene})
                .compactMap({$0})
                .first?.windows
                .filter({$0.isKeyWindow}).first
             transparentView.frame = window?.frame ?? self.view.frame
             self.mainScrollBar.addSubview(transparentView)
             
             tableView.frame = CGRect(x: frames.origin.x, y: frames.origin.y + frames.height, width: frames.width, height: 0)
             self.mainScrollBar.addSubview(tableView)
             tableView.layer.cornerRadius = 5
             
             transparentView.backgroundColor = UIColor.black.withAlphaComponent(0.9)
             tableView.reloadData()
             let tapgesture = UITapGestureRecognizer(target: self, action: #selector(removeTransparentView))
             transparentView.addGestureRecognizer(tapgesture)
             transparentView.alpha = 0
             UIView.animate(withDuration: 0.4, delay: 0.0, usingSpringWithDamping: 1.0, initialSpringVelocity: 1.0, options: .curveEaseInOut, animations: {
                 self.transparentView.alpha = 0.5
                 self.tableView.frame = CGRect(x: frames.origin.x, y: frames.origin.y + frames.height + 5, width: frames.width, height: CGFloat(self.dataSource.count * 50))
             }, completion: nil)
         }
    @objc func removeTransparentView() {
             let frames = selectedButton.frame
             UIView.animate(withDuration: 0.4, delay: 0.0, usingSpringWithDamping: 1.0, initialSpringVelocity: 1.0, options: .curveEaseInOut, animations: {
                 self.transparentView.alpha = 0
                 self.tableView.frame = CGRect(x: frames.origin.x, y: frames.origin.y + frames.height, width: frames.width, height: 0)
             }, completion: nil)
        self.mainScrollBar.setContentOffset(.zero, animated: true)
         }
    
}

class CellClass: UITableViewCell {
    
}
extension AppointmentBookingViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        cell.textLabel?.text = dataSource[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedButton.setTitle(dataSource[indexPath.row], for: .normal)
        removeTransparentView()
    }
}
