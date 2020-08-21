package br.adv.daniel.api_inpi;

import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class MyApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
    	bind( JustInTimeServiceResolver.class ).to( JustInTimeInjectionResolver.class );
    }
}
