//
//  ReviewAppointmentViewController.swift
//  sdk
//
//  Created by Riya Balhara on 02/08/21.
//
import UIKit
import SafariServices
class ReviewAppointmentViewController: UIViewController, SFSafariViewControllerDelegate {
    
    @IBOutlet weak var userEmailId: UILabel!
    @IBOutlet weak var userPhoneNo: UILabel!
    @IBOutlet weak var userName1: UILabel!
    @IBOutlet weak var userName2: UILabel!
    @IBOutlet weak var visitDescriptionText: UITextView!
    @IBOutlet weak var LabelChooseTimeSlot: UILabel!
    @IBOutlet weak var mainScrollBar: UIScrollView!
    @IBOutlet weak var specialistLabel: UILabel!
    @IBOutlet weak var navigationLabel: UILabel!
    @IBOutlet weak var serviceNameLabel: UILabel!
    @IBOutlet weak var servicePriceLabel: UILabel!
    
    @IBOutlet weak var specialityLabel: UILabel!
    
    @IBOutlet weak var navigationRightSpace: UILabel!
    @IBOutlet weak var navigationBackButton: UIButton!
    
    @IBOutlet weak var firstLine:UIView!
    @IBOutlet weak var secondLine:UIView!
    @IBOutlet weak var thirdLine:UIView!
    
    @IBOutlet weak var bookVirtualVisitsBtn: UIButton!
    var organizationId:String?
    public init() {
        super.init(nibName: "ReviewAppointmentViewController", bundle: Bundle(for: ReviewAppointmentViewController.self))
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    var InfoArr : [String : String]? 
    public init(specialistArr : [String : String]) {
        InfoArr = specialistArr;
        super.init(nibName: "ReviewAppointmentViewController", bundle: Bundle(for: ReviewAppointmentViewController.self))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
       
        
        
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
            
            bookVirtualVisitsBtn.backgroundColor = primaryColor
            bookVirtualVisitsBtn.setTitleColor(primaryTextColor, for: .normal)
            firstLine.backgroundColor = primaryColor
            secondLine.backgroundColor = primaryColor
            thirdLine.backgroundColor = primaryColor
          }
        
        specialityLabel.text = InfoArr!["speciality"]
        /* let user_identity = SpriteHealthClient.user_identity   // Constructor
        if(user_identity == "gary@berger.com")
        {
            userName1.text = "Gary Vayenrchuk"
            userName2.text = "Gary Vayenrchuk"
            userEmailId.text = "gary@berger.com"
            userPhoneNo.text = "8150-369-423"
        }
 */
        userName1.text = InfoArr!["memberName"]
        userName2.text = InfoArr!["memberName"]
        userEmailId.text = InfoArr!["memberEmail"]
        userPhoneNo.text = InfoArr!["memberPhone"]
        self.organizationId = InfoArr!["organizationId"]
        //let cmObj = SpriteHealthClient()
        
        
        
       /* cmObj.memberDetail(){ (error, result) in
            // do stuff with the result
            if let error = error {
                print(error)
            } else {
            let resultConverted = result.replacingOccurrences(of: "\\", with: "")
            if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? AnyObject {

              // print(json);
               // print(type(of: json))
                DispatchQueue.main.async { [self] in
                    if let email = json["email"] as? String {
                        self.userEmailId.text = email
                        
                    }
                    if let name = json["name"] as? String {
                        self.userName1.text = name
                        self.userName2.text = name
                    }
                    
                    if let mobilePhone = json["mobilePhone"] as? String {
                        self.userPhoneNo.text = mobilePhone
                    }
                    
                    if let organizationIdVar = json["organizationId"] as? Int64 {
                        self.organizationId = String(organizationIdVar)
                    }
                    
                }
            }
            }
        }
        */
        
     //   let attributedString = NSMutableAttributedString(string: "Want to learn iOS? You should visit the best source of free iOS tutorials!")
                //attributedString.addAttribute(.link, value: "https://www.hackingwithswift.com", range: NSRange(location: 19, length: 55))

        //        policyText.attributedText = attributedString
        
        let startTime = InfoArr?["startTime"]
        let endTime = InfoArr?["endTime"]
        let specialistName  = InfoArr?["specialistName"]
        //var specialistId  = InfoArr?["specialistId"]
        specialistLabel.text = specialistName
        
        serviceNameLabel.text = InfoArr?["serviceName"]
        servicePriceLabel.text = InfoArr?["servicePrice"]
        
        //visitDescriptionText.text =
        let termAndPolicyTxt = "At the time of your visit, you and " + specialistName! + "will meet at the address above. If you would like to change your appointment, you can do that via Slette app or website. By confirming your appointment you are agreeinleto abide by the Terms of Use, Privacy Policy, Consent to Care via Telehealth, and the Sprite Health’s Cancellation policy."
        
        
        let attributedString = NSMutableAttributedString(string: termAndPolicyTxt, attributes: [NSAttributedString.Key.font : UIFont.systemFont (ofSize: 20)])
        var  url = URL(string: "https://spritehealth.com/about-sprite-health/disclaimer-and-terms-of-use/")!
        var termOdUseRange = termAndPolicyTxt.range(of: "Terms of Use")
        
        var convertedRange = NSRange(termOdUseRange!, in: termAndPolicyTxt)
        
        // Set the 'click here' substring to be the link
        attributedString.setAttributes([.link: url,.font : UIFont.systemFont (ofSize: 20)], range: convertedRange)
        
        
        url = URL(string: "https://spritehealth.com/sprite-health-digital-platform-privacy-policy/")!
        termOdUseRange = termAndPolicyTxt.range(of: "Privacy Policy")
        
        convertedRange = NSRange(termOdUseRange!, in: termAndPolicyTxt)
        
        // Set the 'click here' substring to be the link
        attributedString.setAttributes([.link: url,.font : UIFont.systemFont (ofSize: 20)], range: convertedRange)
        
        url = URL(string: "https://spritehealth.com/consent-to-use-of-telehealth/")!
        termOdUseRange = termAndPolicyTxt.range(of: "Consent to Care via Telehealth")
        
        convertedRange = NSRange(termOdUseRange!, in: termAndPolicyTxt)
        
        // Set the 'click here' substring to be the link
        attributedString.setAttributes([.link: url,.font : UIFont.systemFont (ofSize: 20)], range: convertedRange)
        

        self.visitDescriptionText.attributedText = attributedString
        self.visitDescriptionText.isUserInteractionEnabled = true
        self.visitDescriptionText.isEditable = false

        // Set how links should appear: blue and underlined
        self.visitDescriptionText.linkTextAttributes = [
            .foregroundColor: UIColor.blue
               
        ]
       
        let currentPosition = visitDescriptionText.frame
        
        let yForTextView = currentPosition.height + currentPosition.origin.y + 15
        let label = UILabel()
        label.numberOfLines=7
        label.textAlignment = .left;//.center
        label.textColor = .darkGray
        label.font.withSize(20)
        label.text = "Sprite Health isn't a replacement for your doctor or emergency services. If you think you are having an emergency, immediately contact 911 or your country's emergency services number."
        label.backgroundColor = .systemGray6
        label.frame = CGRect(x:10, y: yForTextView, width: currentPosition.width , height: 150)
        
        mainScrollBar.addSubview(label)
        let startTimeArr = startTime!.components(separatedBy: "##")
        let endTimeArr = endTime!.components(separatedBy: "##")
        
        LabelChooseTimeSlot.text = startTimeArr[0] + " " + startTimeArr[1] + " - " + endTimeArr[1]
        mainScrollBar.isUserInteractionEnabled = true;
        let height = Int(self.view.frame.size.height) + 230
        mainScrollBar.contentSize = CGSize(width: Int(mainScrollBar.frame.size.width), height: height)
        
        /*
        var lineView = UIView(frame: CGRect(x: 0, y: 225 , width: mainScrollBar.frame.size.width, height: 1))
        lineView.backgroundColor = UIColor.gray
        mainScrollBar.addSubview(lineView)
        
         lineView = UIView(frame: CGRect(x: 0, y: 330 , width: mainScrollBar.frame.size.width, height: 1))
        lineView.backgroundColor = UIColor.gray
        mainScrollBar.addSubview(lineView)
        lineView = UIView(frame: CGRect(x: 0, y: 520 , width: mainScrollBar.frame.size.width, height: 1))
       lineView.backgroundColor = UIColor.gray
       mainScrollBar.addSubview(lineView)
        
        
         var lineView = UIView(frame: CGRect(x: 20, y: 420 , width: mainScrollBar.frame.size.width-40, height: 1))
        lineView.backgroundColor = UIColor.blue
        mainScrollBar.addSubview(lineView)
        
        lineView = UIView(frame: CGRect(x: 20, y: 460 , width: mainScrollBar.frame.size.width-40, height: 1))
       lineView.backgroundColor = UIColor.blue
       mainScrollBar.addSubview(lineView)
        
        lineView = UIView(frame: CGRect(x: 20, y: 500 , width: mainScrollBar.frame.size.width-40, height: 1))
       lineView.backgroundColor = UIColor.blue
       mainScrollBar.addSubview(lineView)
         */
       
        
        
    }
    
    @IBAction func navigationBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func gobackTomain(_ sender: Any) {
        
        
        print("parent app callback")
        let specialistName  = InfoArr?["specialistName"]
        let specialistId  = InfoArr?["specialistId"]
        let startTime = (InfoArr?["startTimeRaw"] as String?)!
        let endTime = (InfoArr?["endTimeRaw"] as String?)!
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM-dd-yyyy HH:mm:ss"
        let date = dateFormatter.date(from: startTime)
        dateFormatter.dateFormat = "MM/dd/yyyy h:mm a"
        let startTimeAfterMan = dateFormatter.string(from: date!)
        dateFormatter.dateFormat =  "MM-dd-yyyy HH:mm:ss"
        let date2 = dateFormatter.date(from: endTime)
        dateFormatter.dateFormat = "MM/dd/yyyy h:mm a"
        let endTimeAfterMan = dateFormatter.string(from: date2!)
        
        
        let requestParameters: [String: Any] = [
            "vendorUserId": (specialistId as String?)!,
                "serviceId": 6207822929854464,
                "providerName": (specialistName as String?)!,
                "serviceName": "Virtual Physical Therapy",
                "eventStartTime": startTimeAfterMan,
                "eventEndTime": endTimeAfterMan,
                "status": "booked",
                "customerPhone": "+91 9717266384",
                "serviceSetting": "Telehealth",
                "where": "Detail evaluation and assessment of current orthopedic issues. Advice on treatment will be provided to include self-management techniques, expectations of injury, and how to progress home exercise program.",
                "patientId": 6282717383622656
            ]
       // print(requestParameters)
        
        let myActivityIndicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                       
                       // Position Activity Indicator in the center of the main view
                       myActivityIndicator.center = view.center
               
                       // If needed, you can prevent Acivity Indicator from hiding when stopAnimating() is called
                       myActivityIndicator.hidesWhenStopped = true
                       
                       // Start Activity Indicator
                       myActivityIndicator.startAnimating()
               view.addSubview(myActivityIndicator)
        
       // var urlString = "https://berger.spritehealth.com/globals/appointments/4620834711797760?#access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkYWdAYmVyZ2VyLmNvbSIsImNsaWVudElkIjoiMGI1YzhkNzJmOTc5NGVjNjk4NzA4ODZjZDA2MGJjODIiLCJyb2xlIjoiQ0UiLCJhdXRoR3JhbnRUeXBlIjoiaW1wbGljaXQiLCJpc3MiOiJodHRwczpcL1wvb2F1dGgtZG90LXdwYmFja2VuZHFhLmFwcHNwb3QuY29tXC9yZXNvdXJjZXNcL29hdXRoXC9hdXRoZW50aWNhdGUiLCJ0aW1lWm9uZSI6IkFtZXJpY2FcL0NoaWNhZ28iLCJ1c2VyQWdlbnQiOiJNb3ppbGxhXC81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXRcLzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZVwvOTIuMC40NTE1LjEzMSBTYWZhcmlcLzUzNy4zNiIsInVzZXJJZCI6NjI4MjcxNzM4MzYyMjY1NiwiYXVkIjoiaHR0cHM6XC9cL29hdXRoLWRvdC13cGJhY2tlbmRxYS5hcHBzcG90LmNvbSIsIm5iZiI6MTYyODc3MDg1NSwic2NvcGUiOiJ1c2VyX3JlYWQgYXBwb2ludG1lbnRfcmVhZCBhcHBvaW50bWVudF93cml0ZSIsImlzU1NPIjpmYWxzZSwiZXhwIjoxNjI4ODU3MjU1LCJhdXRoVHlwZSI6Ik5BVElWRSIsImlhdCI6MTYyODc3MDg1NSwianRpIjoiM2Q2MmNhZWMtOTc1ZC00ODlkLTljZGItNzE3NzY0MGYyY2VhIiwiZW1haWwiOiJkYWdAYmVyZ2VyLmNvbSJ9.lhKN2EJ58z7weoeDCxDZG5EqQ3t_Xn9IqNxnhAVgnRkPQ50Dba6ghZw-cWcunrWD0cK_cHJ2nIPpIbJOV85WGaPGW4iuS1LR1A63dsVu1W9ZN6wR667QA3HPTTj0V2N-QLNRNoo9sxm3OUUr_CGbohI-M-NoDwqQakcNg9nPBMgKipwAKsG8INFcnFIfd-Jxp3L2q-i-GlL8wAIht7PMzCix5CbTVHX7AarqTh55_kDnteQ90gzntUAwL6fjJWe6Afo7Oey8XxvFncF6fK12PwqHey6qezUkbKcJbKOxRNWCB9FFH2WiJ2SFcCmiMLOdySaXlWAy5loRa7wkTGwGUA"
       // print(urlString)
      
           
              
       
            let shObject = SpriteHealthClient()   // Constructor
            shObject.appointmentBooking(requestParameters: requestParameters){ (error, result) in
                if let error = error {
                print(error)
                } else {
                // do stuff with the result
                //print(result)
                    let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String:Any]
                        {

                          // print(json);
                        if let appointment = json["appointment"] as? NSDictionary {
                            
                            
                            let appointmentId = appointment["id"] as? Int
                            DispatchQueue.main.async {
                                myActivityIndicator.stopAnimating()
                               
                                
                                var InfoArr : [String : String]? = [ : ]
                                InfoArr!["appointmentId"] = String(appointmentId!)
                                var authToken =  shObject.getToken()
                                /*let requestParameters: [String: Any] = [
                                    "accessToken": authToken,
                                        "ssoLandingURL": "https://oauth-dot-wpfrontendqa.appspot.com/globals/appointments/"+String(appointmentId!)
                                        
                                    ]*/
                                
                               
                               
                                DispatchQueue.main.async {
                                    var urlString: String = "https://wpfrontendqa.appspot.com/globals/appointments/<appointmentId>?" + "wlb=true%3Bimplicit%3B" + self.organizationId! + "#access_token=<access_token>"
                                    authToken = authToken.replacingOccurrences(of: "Bearer ", with: "")
                                    urlString = urlString.replacingOccurrences(of: "<access_token>", with: authToken)
                                    urlString = urlString.replacingOccurrences(of: "<appointmentId>", with: String(appointmentId!))
                                    print(urlString)
                                    if let url = URL(string: urlString) {
                                        let vc = SFSafariViewController(url: url)
                                        vc.preferredBarTintColor = SpriteHealthClient.hexStringToUIColor(hex:SpriteHealthClient.primaryColor)
                                        vc.preferredControlTintColor = SpriteHealthClient.hexStringToUIColor(hex:SpriteHealthClient.primaryTextColor)
                                        vc.delegate = self

                                        self.present(vc, animated: true)
                                    }
                               /* let newViewController = AppointmentDetailViewController(specialistArr: InfoArr!)
                                newViewController.modalPresentationStyle = .fullScreen
                                self.present(newViewController, animated:true,completion:nil)
                                   
                               */
                                }
                            }
                        }
                            
                            //SpriteHealthClient.gobackToParent();
                    }
                }
            }
            
      
        
    }
    func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        dismiss(animated: false)
        SpriteHealthClient.gobackToParent();
        
          
        self.presentingViewController?.presentingViewController?.presentingViewController?.presentingViewController?.dismiss(animated: false, completion: nil)
    }
    
}



