/*
 * SelectRooms.java
 *
 * Created on January 12th, 2003, 01:25
 */

package ServidorAplicacao.Servico.publico;

/**
 * Service SelectRooms.
 *
 * @author tfc130
 **/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DataBeans.InfoRoom;
import DataBeans.util.Cloner;
import Dominio.ISala;
import ServidorAplicacao.IServico;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

public class SelectRooms implements IServico {
	private static SelectRooms _servico = new SelectRooms();
	/**
	 * The singleton access method of this class.
	 **/
	public static SelectRooms getService() {
	  return _servico;
	}

	/**
	 * The actor of this class.
	 **/
	private SelectRooms() { }

	/**
	 * Devolve o nome do servico
	 **/
	public final String getNome() {
	  return "SelectRooms";
	}

	/**
	 * The run method of this Service class.
	 **/
	public Object run(InfoRoom InfoRoom) {
	  List salas = null;

	  try {
		ISuportePersistente sp = SuportePersistenteOJB.getInstance();
		Integer tipo = InfoRoom.getTipo() != null ? InfoRoom.getTipo().getTipo() : null;

		salas = sp.getISalaPersistente().readSalas(InfoRoom.getNome(), InfoRoom.getEdificio(),
						   InfoRoom.getPiso(), tipo,
						   InfoRoom.getCapacidadeNormal(),
						   InfoRoom.getCapacidadeExame());
	  } catch (ExcepcaoPersistencia ex) {
		ex.printStackTrace();
		return null;
	  }

	  if (salas == null)
		return new ArrayList();

	  Iterator iter = salas.iterator();
	  ArrayList salasView = new ArrayList();
	  ISala sala;

	  while (iter.hasNext()) {
		sala = (ISala)iter.next();
		salasView.add(Cloner.copyRoom2InfoRoom(sala));
//		salasView.add(new InfoRoom(sala.getNome(), sala.getEdificio(),
//				   sala.getPiso(), sala.getTipo(),
//				   sala.getCapacidadeNormal(), sala.getCapacidadeExame()));
	  }

	  return salasView;
	}
 
}