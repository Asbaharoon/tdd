package leilao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.exception.LanceIgualAZeroException;
import br.com.caelum.leilao.exception.LanceNegativoException;
import br.com.caelum.leilao.exception.LanceSeguidoException;
import br.com.caelum.leilao.exception.LimiteLanceException;
import br.com.caelum.leilao.exception.UltimoLanceIndisponivelException;
import org.junit.Test;

public class LeilaoTest extends LeilaoTestHelper {

	@Test
	public void naoDeveAceitarDoisLancesSeguidosDoMesmoUsuario() throws LanceNegativoException, LanceIgualAZeroException {
		Lance lance1 = criaLance(joao, BigDecimal.TEN);
		Lance lance2 = criaLance(joao, BigDecimal.TEN.add(BigDecimal.TEN));

		try {
			leilao.propoe(lance1);
			leilao.propoe(lance2);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
			assertEquals(e.getClass(), LanceSeguidoException.class);
		}
	}

	@Test
	public void naoDeveAceitarMaisDoQue5LancesDeUmMesmoUsuario() throws LanceNegativoException, LanceIgualAZeroException {
		Lance lance1 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance2 = criaLance(maria, getRandomBigDecimal(1, 50));
		Lance lance3 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance4 = criaLance(maria, getRandomBigDecimal(1, 50));
		Lance lance5 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance6 = criaLance(maria, getRandomBigDecimal(1, 50));
		Lance lance7 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance8 = criaLance(maria, getRandomBigDecimal(1, 50));

		// 5� lance
		Lance lance9 = criaLance(joao, getRandomBigDecimal(1, 50));

		List<Lance> listaLances = Arrays.asList(lance1, lance2, lance3, lance4, lance5, lance6, lance7, lance8, lance9);

		try {
			for (Lance lance : listaLances) {
				leilao.propoe(lance);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
			assertEquals(e.getClass(), LimiteLanceException.class);
		}
	}

	@Test
	public void validaDobraDeLance() throws Exception {
		Lance lance1 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance2 = criaLance(maria, getRandomBigDecimal(1, 50));
		Lance lance3 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance4 = criaLance(maria, getRandomBigDecimal(1, 50));
		Lance lance5 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance6 = criaLance(roberto, getRandomBigDecimal(1, 50));
		Lance lance7 = criaLance(joao, getRandomBigDecimal(1, 50));
		Lance lance8 = criaLance(jose, getRandomBigDecimal(1, 50));
		Lance lance9 = criaLance(maria, getRandomBigDecimal(1, 50));

		List<Lance> listaLances = Arrays.asList(lance1, lance2, lance3, lance4, lance5, lance6, lance7, lance8, lance9);

		leilao.propoe(listaLances);

		// Situa��o 1: dobra o lance da Maria, mas o �ltimo lance foi dela (LanceSeguidoException)
		Exception thrownException = null;

		try {
			leilao.dobra(maria);
		} catch (LanceSeguidoException e) {
			LOG.log(Level.SEVERE, e.getMessage());
			thrownException = e;
		} finally {
			assertNotNull(thrownException);
			assertEquals(thrownException.getClass(), LanceSeguidoException.class);
		}

		// Situa��o 2: dobra o lance do Jo�o, mas ele j� atingiu o limite de 5 lan�amentos (LimiteLancamentoException)
		thrownException = null;

		try {
			leilao.dobra(joao);
		} catch (LimiteLanceException e) {
			LOG.log(Level.SEVERE, e.getMessage());
			thrownException = e;
		} finally {
			assertNotNull(thrownException);
			assertEquals(thrownException.getClass(), LimiteLanceException.class);
		}

		// Situa��o 3: dobra o lance de Roberto, por�m ele n�o teve lances (UltimoLanceIndisponivelException)
		thrownException = null;

		try {
			leilao.dobra(lucas);
		} catch (UltimoLanceIndisponivelException e) {
			LOG.log(Level.SEVERE, e.getMessage());
			thrownException = e;
		} finally {
			assertNotNull(thrownException);
			assertEquals(thrownException.getClass(), UltimoLanceIndisponivelException.class);
		}
		// Situa��o 4: dobra o lance do Jos� com sucesso
		BigDecimal valorLanceOriginalDobrado = lance8.getValor().multiply(BigDecimal.valueOf(2));
		Lance lanceDobrado;

		try {
			leilao.dobra(jose);
		} catch (LimiteLanceException e) {
			LOG.log(Level.SEVERE, e.getMessage());
			fail();
		}

		lanceDobrado = leilao.getUltimoLance();

		assertEquals(valorLanceOriginalDobrado, lanceDobrado.getValor());
	}
}
