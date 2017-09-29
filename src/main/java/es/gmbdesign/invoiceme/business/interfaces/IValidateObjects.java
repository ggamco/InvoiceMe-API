package es.gmbdesign.invoiceme.business.interfaces;

/**
 * Interfaz funcional que permite la validación usando expresiones lambda. deja abierta la posibilidad de 2 tipos distintos de objetos.
 * 
 * @author gustavo.gamboa
 *
 * @param <T>
 * @param <V>
 */
public interface IValidateObjects <T, V> {
	/**
	 * Método de interfaz funcional que nos permite validar 2 objetos del mismo tipo.
	 * 
	 * @param arg0 -> Tipo 1
	 * @param arg1 -> Tipo 2
	 * @return
	 */
	boolean validate(T arg0, V arg1);
}
