//
//  SpecialistDetailViewController.swift
//  sdk
//
//  Created by Riya Balhara on 29/06/21.
//

import UIKit

struct detailViewModel {
    let primaryText : String
    let secondaryText: String
    let numberOfLines: Int
   
}

 class detailLabel: UIButton {
        private let primaryLabel: UILabel = {
            let label = UILabel()
            label.numberOfLines=1
            label.textAlignment = .left;//.center
            label.textColor = .lightGray
            label.font = UIFont.boldSystemFont(ofSize: 20)
            //label.font = .systemFont(ofSize: 20, weight: .semibold)
            return label
        }()
    private let secondaryLabel: UITextView = {
        let label = UITextView()
        
        //label.textAlignment = .left;//.center
        label.textColor = .black
        
        label.font = .systemFont(ofSize: 20)
        //label.font = .systemFont(ofSize: 20, weight: .regular)
        //label.numberOfLines = 0
        label.sizeToFit()
        
        return label
    }()
    
    
   
    

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(primaryLabel)
        addSubview(secondaryLabel)
        
        clipsToBounds = true
        //layer.cornerRadius = 8
        //layer.borderWidth = 2
        //layer.borderColor = UIColor.systemGray5.cgColor
        //backgroundColor = .systemBackground
        backgroundColor = .white
        //backgroundColor = UIColor.init(red: 100, green: 200, blue: 240, alpha: 1)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func configure(with viewModel:detailViewModel)
    {
        primaryLabel.text = viewModel.primaryText
        secondaryLabel.text = viewModel.secondaryText
        
       /* if( viewModel.numberOfLines == 0){
            secondaryLabel.numberOfLines = 0
            secondaryLabel.sizeToFit()
        }
        else {
            secondaryLabel.numberOfLines = viewModel.numberOfLines
        }
 */
        
    }
    
 

    override func layoutSubviews() {
        super.layoutSubviews()
       
        primaryLabel.frame = CGRect(x: 0, y: 0, width: frame.size.width, height: 30)
        secondaryLabel.frame = CGRect(x: 0, y: 30, width: frame.size.width, height: frame.size.height-30    )
      
    }
}


class SpecialistDetailViewController: UIViewController {
    
    @IBOutlet weak var specialityLabel: UILabel!
    @IBOutlet weak var detailContentView: UIView!
    @IBOutlet weak var detailsStackView: UIStackView!
    @IBOutlet weak var genderLabel: UILabel!
    @IBOutlet weak var qualificationLabel: UILabel!
    @IBOutlet weak var navBar: UINavigationBar!
    @IBOutlet weak var educationLabel: UILabel!
    @IBOutlet weak var briefSummaryLabel: UILabel!
    @IBOutlet weak var navigationRightSpace: UILabel!
    @IBOutlet weak var navigationBackButton: UIButton!
    var specialistId:String?
    var specialistName:String?
    var specialistImageUrl:String?
    var specialistArrIncoming: [String :String]?
    var serviceId: String?
    var speciality: String?
    @IBOutlet weak var viewInsideScrollView: UIView!
    @IBOutlet weak var detailScrollView: UIScrollView!
    @IBOutlet weak var specialistImage: UIImageView!
    @IBOutlet weak var specialistLabel: UILabel!
    public init(specialistArr : [String : String]) {
        specialistArrIncoming = specialistArr
        specialistId = specialistArr["id"]
        specialistName = specialistArr["name"]
        specialistImageUrl = specialistArr["imageUrl"]
        serviceId = specialistArr["serviceId"]
        speciality = specialistArr["speciality"]
        
        super.init(nibName: "SpecialistDetailViewController", bundle: Bundle(for: SpecialistDetailViewController.self))
    }
    /*func convertToDictionary(text: String) -> Any? {

     if let data = text.data(using: .utf8) {
         
            // return try JSONSerialization.jsonObject(with: data, options: []) as? Any
         
            let json = try? JSONSerialization.jsonObject(with: data as Data, options: []) as? [String:Any]
            return json
        
     }

     return nil
    }*/
    
    
    @IBOutlet weak var navigationLabel: UILabel!
    @IBOutlet weak var bookAppointmentbtn: UIButton!
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
            bookAppointmentbtn.backgroundColor = primaryColor
             
          }
        
        
        
        specialityLabel.text = speciality
        //detailScrollView.contentSize = CGSize(width: Int(self.view.frame.size.width), height: 500)
       // detailContentView.superview?.bringSubviewToFront(detailContentView)
        //detailsStackView.superview?.bringSubviewToFront(detailsStackView)
        view.isUserInteractionEnabled = false
        let myActivityIndicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                       
                       // Position Activity Indicator in the center of the main view
                       myActivityIndicator.center = view.center
               
                       // If needed, you can prevent Acivity Indicator from hiding when stopAnimating() is called
                       myActivityIndicator.hidesWhenStopped = true
                       
                       // Start Activity Indicator
                       myActivityIndicator.startAnimating()
               view.addSubview(myActivityIndicator)
        
     
        if let member = SpriteHealthClient.memberInfo {
                DispatchQueue.main.async { [self] in
                   
                        specialistArrIncoming?["memberEmail"] = member.email
                   
                    if  member.name != nil {
                       
                        specialistArrIncoming?["memberName"] = member.name!
                    }
                  
                        specialistArrIncoming?["memberId"] = String(member.id)
                    
                    if member.mobilePhone != nil  {
                        specialistArrIncoming?["memberPhone"] = member.mobilePhone!
                       
                    }
                    
                    if member.organizationId != nil   {
                        specialistArrIncoming?["organizationId"] = String(member.organizationId!)
                        
                    }
                    
                }
            }
            
         
                
       
        specialistLabel.text = specialistName
        let cmObj = SpriteHealthClient.getInstance()
        cmObj.getSpecialistDetails(specialistId: Int(specialistId!)!){ (error, result) in
            if let error = error {
                print(error)
            } else {
            
                DispatchQueue.main.async {
                self.displayDetails(specialist: result)
                    myActivityIndicator.stopAnimating()
                    self.view.isUserInteractionEnabled = true;
                }
           
            }
        }
        
        let urlString = specialistImageUrl!
        specialistImage.downloaded(from: urlString)
        // Do any additional setup after loading the view.
    }
    @IBAction func navigationBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    
    func displayDetails(specialist: Specialist?) {
        detailScrollView.isScrollEnabled = true
        detailScrollView.isUserInteractionEnabled = true
        //let screenSize: CGRect = UIScreen.main.bounds
        let xTopLeft = 0
        
        let topmargin = 0
        var count = 0;
        var briefSummary = ""
        var genderTxt = "";
        var educationTxt = ""
        var qualificationTxt = ""
        var professionalExperienceTxt = ""
        var languageTxt = ""
        var registrationTxt = ""
        var awardTxt = ""
        var membershipTxt = "";
        if specialist?.gender != nil {
            //genderLabel.text = gender
            //AddLebelAndDescInScrollView(labelTxt:"GENDER", desc:gender)
            genderTxt = (specialist?.gender!)!;
            }
    
        var educationCount = 0;
        if specialist?.descriptions != nil {
           
            
            for description in (specialist?.descriptions!)!
            {
                if (description.vendorDescriptionType == "EDUCATION"){
                    let education =  description.description!
                    
                    let multieducationArr: [String] = education.components(separatedBy: "%%")
                    
                    if(multieducationArr.count > 0)
                    {
                          for education in multieducationArr
                          {
                            let educationArr: [String] = education.components(separatedBy: "$$")
                            //print(type(of: educationArr))
                            if(educationArr.count > 0)
                            {
                                var educationLabeltxt = "";
                                if(educationArr.count == 3)
                                {
                                    educationLabeltxt = educationArr[0] + " from " + educationArr[1] + " in " + educationArr[2]
                                    
                                }
                                
                                else if(educationArr.count == 2)
                                {
                                    educationLabeltxt = educationArr[0] + " from " + educationArr[1]
                                }
                                if(educationTxt  == "")
                                {
                                    educationTxt = educationLabeltxt
                                    educationCount =  educationCount + 1
                                }
                                else {
                                    educationTxt = educationTxt + "\n" + educationLabeltxt
                                    educationCount = educationCount +  1
                                }
                                
                            }
                          }
                    }
                }
                if (description.vendorDescriptionType == "SHORT_DESCRIPTION"){
                    briefSummary = description.description!
                   
                }
                if (description.vendorDescriptionType == "QUALIFICATION"){
                    qualificationTxt = description.description!
                  
                }
                if (description.vendorDescriptionType == "PROFESSIONAL_EXPERIENCE"){
                    professionalExperienceTxt = description.description!
                  
                }
                if (description.vendorDescriptionType == "LANGUAGES"){
                    languageTxt = description.description!
                  
                }
                if (description.vendorDescriptionType == "REGISTRATION"){
                    registrationTxt = description.description!
                  
                }
                if (description.vendorDescriptionType == "AWARDS_AND_RECOGNITIONS"){
                    awardTxt = description.description!
                  
                }
                
                if (description.vendorDescriptionType == "MEMBERSHIPS"){
                    membershipTxt = description.description!
                  
                }
                
            }
         
          
        }
        
        let detailScrollViewWidth = Int(detailScrollView.frame.size.width)
        var currentHeight = 0;
        if(genderTxt != "")
        {
            let yTopLeft = topmargin + count*70
            let button = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: 60))
            button.configure(with: detailViewModel(primaryText: "GENDER", secondaryText: genderTxt, numberOfLines:1))
            viewInsideScrollView.addSubview(button)
            count += 1
            currentHeight = topmargin + count*70
        }
        if(qualificationTxt != ""){
            let yTopLeft = currentHeight
            let qualLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: 60))
            qualLabel.configure(with: detailViewModel(primaryText: "QUALIFICATION", secondaryText: qualificationTxt,numberOfLines:1))
            viewInsideScrollView.addSubview(qualLabel)
            count += 1
            currentHeight = currentHeight + 70
        }
        //var offset = 0;
        var textHeight:CGFloat = 0
        if(educationTxt != "") {
            let yTopLeft = currentHeight
            textHeight = educationTxt.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22)) + CGFloat((educationCount - 1) * 30);
           
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: Int(textHeight)))
            eduLabel.configure(with: detailViewModel(primaryText: "EDUCATION", secondaryText: educationTxt,numberOfLines : 0))
            viewInsideScrollView.addSubview(eduLabel)
            count += 1
            
            currentHeight = currentHeight + Int(textHeight) + 10
        }
        
        
        if(briefSummary != "") {
            
            textHeight = briefSummary.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22));
           
            let yTopLeft = currentHeight
            let briefSummaryLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: Int(textHeight)))
            briefSummaryLabel.configure(with: detailViewModel(primaryText: "BRIEF SUMMARY", secondaryText: briefSummary,numberOfLines:0))
            viewInsideScrollView.addSubview(briefSummaryLabel)
            currentHeight = currentHeight + Int(textHeight)
        }
       
        if(professionalExperienceTxt != "") {
            let yTopLeft = currentHeight
            textHeight = professionalExperienceTxt.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22));
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: Int(textHeight)))
            eduLabel.configure(with: detailViewModel(primaryText: "PROFESSIONAL_EXPERIENCE", secondaryText: professionalExperienceTxt,numberOfLines : 0))
            viewInsideScrollView.addSubview(eduLabel)
            currentHeight = currentHeight + Int(textHeight) + 10
           
        }
        if(languageTxt != "") {
            let yTopLeft = currentHeight
            textHeight = languageTxt.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22)) + 30 ;
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height:  Int(textHeight)))
            eduLabel.configure(with: detailViewModel(primaryText: "LANGUAGES", secondaryText: languageTxt,numberOfLines : 0))
            viewInsideScrollView.addSubview(eduLabel)
            currentHeight = currentHeight + Int(textHeight) + 10
           
        }
        
        if(registrationTxt != "") {
            let yTopLeft = currentHeight
            textHeight = registrationTxt.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22)) ;
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: Int(textHeight)))
            eduLabel.configure(with: detailViewModel(primaryText: "REGISTRATION", secondaryText: registrationTxt,numberOfLines : 0))
            viewInsideScrollView.addSubview(eduLabel)
            currentHeight = currentHeight + Int(textHeight) + 10
           
        }
        
        if(awardTxt != "") {
            let yTopLeft = currentHeight
            textHeight = awardTxt.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22)) ;
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: Int(textHeight)))
            eduLabel.configure(with: detailViewModel(primaryText: "AWARDS_AND_RECOGNITIONS", secondaryText: awardTxt,numberOfLines : 0))
            viewInsideScrollView.addSubview(eduLabel)
            currentHeight = currentHeight + Int(textHeight) + 10
           
        }
        if(membershipTxt != "") {
            let yTopLeft = currentHeight
            textHeight = membershipTxt.height(constraintedWidth: CGFloat(detailScrollViewWidth), font: UIFont.systemFont(ofSize: 22)) ;
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: detailScrollViewWidth, height: Int(textHeight)))
            eduLabel.configure(with: detailViewModel(primaryText: "MEMBERSHIPS", secondaryText: membershipTxt,numberOfLines : 0))
            viewInsideScrollView.addSubview(eduLabel)
            currentHeight = currentHeight + Int(textHeight) + 10
           
        }
        
        let height = currentHeight + 50
        //detailScrollView.contentSize = CGSize(width: Int(screenSize.width), height: 1000)
        detailScrollView.contentSize = CGSize(width: Int(detailScrollView.frame.size.width), height: height)
        
    }
    @IBAction func ParentAppCalled(_ sender: Any) {
        
      /*  print("parent app callback")
      SpriteHealthClient.gobackToParent();
    
        self.presentingViewController?.presentingViewController?.dismiss(animated: true, completion: nil)
        */
        let newViewController = AppointmentBookingViewController(specialistArr: specialistArrIncoming!)
        newViewController.modalPresentationStyle = .fullScreen
        self.present(newViewController, animated:true,completion:nil)
         
    }
    
    
    public init() {
        super.init(nibName: "SpecialistDetailViewController", bundle: Bundle(for: SpecialistDetailViewController.self))
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
   
}


extension String {
func height(constraintedWidth width: CGFloat, font: UIFont) -> CGFloat {
    let label =  UILabel(frame: CGRect(x: 0, y: 0, width: width, height: .greatestFiniteMagnitude))
    label.numberOfLines = 0
    label.text = self
    label.font = font
    label.sizeToFit()

    return label.frame.height
 }
}
   
