package isel.ps.dwp.templates

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class templatesTests {
        /*
    @Test
    fun testReadTemplateToObject() {
            val utils = TemplatesUtils()
            val json = """{
             "nome": "user",
                    "autor": "user@mail.com",
                    "descricao": "first test",
                    "data_inicio": "2023-05-05",
                    "data_fim": null,
                    "prazo": "30",
                    "estado": "Pending",
                    "template_processo": "FUC",
                    "etapas": [
                    {
                            "nome": "etapa1",
                            "responsavel": ["email1@mail.com", "email2@mail.com"],
                            "descricao": "first stage",
                            "data_inicio": "2023-05-06",
                            "data_fim": null,
                            "prazo": "10",
                            "estado": "Pending"
                    },
                    {
                            "nome": "etapa2",
                            "responsavel": ["email3@mail.com", "email4@mail.com", "email5@mail.com"],
                            "descricao": "second stage",
                            "data_inicio": "2023-05-07",
                            "data_fim": null,
                            "prazo": "20",
                            "estado": "Pending"
                    }
                    ]
            }
        """

        // Act
        val result = utils.parseTemplateToObject(json)

        // Assert
        assertEquals("user", result.nome)
        assertEquals("user@mail.com", result.autor)
        assertEquals("first test", result.descricao)
        assertEquals("2023-05-05", result.data_inicio)
        assertNull(result.data_fim)
        assertEquals("30", result.prazo)
        assertEquals("Pending", result.estado)
        assertEquals("FUC", result.template_processo)
        assertEquals(2, result.etapas.size)
        assertEquals("etapa1", result.etapas[0].nome)
        assertArrayEquals(arrayOf("email1@mail.com", "email2@mail.com"), result.etapas[0].responsavel.toTypedArray())
        assertEquals("first stage", result.etapas[0].descricao)
        assertEquals("2023-05-06", result.etapas[0].data_inicio)
        assertNull(result.etapas[0].data_fim)
        assertEquals("10", result.etapas[0].prazo)
        assertEquals("Pending", result.etapas[0].estado)
        assertEquals("etapa2", result.etapas[1].nome)
        assertArrayEquals(arrayOf("email3@mail.com", "email4@mail.com", "email5@mail.com"), result.etapas[1].responsavel.toTypedArray())
        assertEquals("second stage", result.etapas[1].descricao)
        assertEquals("2023-05-07", result.etapas[1].data_inicio)
        assertNull(result.etapas[1].data_fim)
        assertEquals("20", result.etapas[1].prazo)
        assertEquals("Pending", result.etapas[1].estado)
    }

         */
}