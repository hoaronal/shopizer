package com.salesmanager.shop.populator.store;

import java.util.List;

import org.drools.core.util.StringUtils;
import org.jsoup.helper.Validate;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.references.PersistableAddress;
import com.salesmanager.shop.model.shop.PersistableMerchantStore;

public class PersistableMerchantStorePopulator extends AbstractDataPopulator<PersistableMerchantStore, MerchantStore> {

	private CountryService countryService;

	private ZoneService zoneService;
	
	private LanguageService languageService;
	
	private CurrencyService currencyService;
	
	
	@Override
	public MerchantStore populate(PersistableMerchantStore source, MerchantStore target, MerchantStore store,
			Language language) throws ConversionException {
	
		Validate.notNull(countryService, "CountryService must not be null");
		Validate.notNull(zoneService, "ZoneService must not be null");
		Validate.notNull(languageService, "LanguageService must not be null");
		Validate.notNull(currencyService, "CurrencyService must not be null");
		Validate.notNull(source, "PersistableMerchantStore mst not be null");
		
		if(target == null) {
			target = new MerchantStore();
		}
		
		target.setCode(source.getCode());
		target.setId(source.getId());
		target.setDateBusinessSince(source.getInBusinessSince());
		target.setSeizeunitcode(source.getDimension().name());
		target.setWeightunitcode(source.getWeight().name());
		target.setCurrencyFormatNational(source.isCurrencyFormatNational());
		target.setStorename(source.getName());
		target.setStorephone(source.getPhone());
		target.setStoreEmailAddress(source.getEmail());
		
		try {
			
			if(!StringUtils.isEmpty(source.getDefaultLanguage())) {
				Language l = languageService.getByCode(source.getDefaultLanguage());
				target.setDefaultLanguage(l);
			}
			
			if(!StringUtils.isEmpty(source.getCurrency())) {
				Currency c = currencyService.getByCode(source.getCurrency());
				target.setCurrency(c);
			} else {
				target.setCurrency(currencyService.getByCode(Constants.DEFAULT_CURRENCY.getCurrencyCode()));
			}
			
			List<String> languages = source.getSupportedLanguages();
			for(String lang : languages) {
				Language ll = languageService.getByCode(lang);
				target.getLanguages().add(ll);
			}
			
		} catch(Exception e) {
			throw new ConversionException(e);
		}
		
		//address population
		PersistableAddress address = source.getAddress();
		if(address != null) {
			Country country;
			try {
				country = countryService.getByCode(address.getCountry());

				Zone zone = zoneService.getByCode(address.getStateProvince());
				if(zone != null) {
					target.setZone(zone);
				} else {
					target.setStorestateprovince(address.getStateProvince());
				}
				
				target.setStoreaddress(address.getAddress());
				target.setStorecity(address.getCity());
				target.setCountry(country);
				target.setStorepostalcode(address.getPostalCode());
				
			} catch (ServiceException e) {
				throw new ConversionException(e);
			}
		}
		
		return target;
	}

	@Override
	protected MerchantStore createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZoneService getZoneService() {
		return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}
	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}


}
