package com.hastanerandevu.beans;

import com.hastanerandevu.converter.PasswordEncryptor;
import com.hastanerandevu.enums.BloodGroupEnum;
import com.hastanerandevu.enums.GenderEnum;
import com.hastanerandevu.enums.SecretQuestionEnum;
import com.hastanerandevu.model.PatientModel;
import com.hastanerandevu.service.PatientServiceImpl;
import com.hastanerandevu.utility.SessionUtils;
import com.hastanerandevu.validation.CaptchaValidator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean (name = "patient")
@SessionScoped
public class PatientBean implements Serializable {
  private PatientServiceImpl patientService = new PatientServiceImpl();
  private PatientModel patientModel = new PatientModel();

  private BloodGroupEnum[] bloodGroupEnums = BloodGroupEnum.values();
  private GenderEnum[] genderEnums = GenderEnum.values();
  private SecretQuestionEnum[] secretQuestionEnums = SecretQuestionEnum.values();

  private int loginCounter = 0;
  private boolean showCaptcha = false;
  private boolean verifyCaptcha = false;
  private boolean verifyLogin = false;

  public boolean isVerifyLogin () {
    return verifyLogin;
  }

  public void setVerifyLogin (boolean verifyLogin) {
    this.verifyLogin = verifyLogin;
  }

  public boolean isShowCaptcha () {
    return showCaptcha;
  }

  public PatientModel getPatientModel () {
    return patientModel;
  }

  public void setPatientModel (PatientModel patientModel) {
    this.patientModel = patientModel;
  }

  public BloodGroupEnum[] getBloodGroupEnums () {
    return bloodGroupEnums;
  }

  public GenderEnum[] getGenderEnums () {
    return genderEnums;
  }

  public SecretQuestionEnum[] getSecretQuestionEnums () {
    return secretQuestionEnums;
  }

  public String validateLogin () throws IOException {
    loginCounter += 1;
    if ( loginCounter > 2 ) {
      showCaptcha = true;
    }
    if ( showCaptcha ) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      verifyCaptcha = CaptchaValidator.validate(facesContext);
    }

    verifyLogin = patientService.loginPatient(patientModel);
    if ( showCaptcha ) {
      if ( verifyCaptcha ) {
        return "view/dashboard?faces-redirect=true";
      } else {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bilgileri kontrol ediniz", null));
        return "/";
      }
    }
    if ( verifyLogin ) {
      return "view/dashboard?faces-redirect=true";
    } else {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bilgileri kontrol ediniz", null));
      return "/";
    }
  }

  public String validateCreate () {
    patientModel.setPassword(PasswordEncryptor.encryptPassword(patientModel.getPassword()));
    patientService.create(patientModel);
    return "/";
  }

  public String logout () {
    HttpSession session = SessionUtils.getSession();
    session.invalidate();
    return "/index?faces-redirect=true";
  }

}
