package com.alura.challenalura;

import com.alura.challenalura.dto.BookResponseDTO;
import com.alura.challenalura.model.AuthorEntity;
import com.alura.challenalura.model.BookEntity;
import com.alura.challenalura.service.BookService;
import com.alura.challenalura.service.GutendexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
public class ChallenaluraApplication implements CommandLineRunner {

	private final GutendexService gutendexService;
	private final BookService bookService;
	private final Scanner scanner;

	public ChallenaluraApplication(GutendexService gutendexService, BookService bookService) {
		this.gutendexService = gutendexService;
		this.bookService = bookService;
		this.scanner = new Scanner(System.in);
	}

	public static void main(String[] args) {
		SpringApplication.run(ChallenaluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		mostrarMenu();
		scanner.close();
	}

	private void mostrarMenu() {
		int opcion;
		do {
			System.out.println("\n=== MENÚ PRINCIPAL ===");
			System.out.println("1. Buscar libro por título");
			System.out.println("2. Búsqueda avanzada de libros");
			System.out.println("3. Listar todos los libros registrados");
			System.out.println("4. Listar libros por idioma");
			System.out.println("5. Listar todos los autores");
			System.out.println("6. Buscar autores vivos en un año");
			System.out.println("7. Mostrar estadísticas por idioma");
			System.out.println("8. Salir");
			System.out.print("Seleccione una opción: ");

			try {
				opcion = Integer.parseInt(scanner.nextLine());

				switch (opcion) {
					case 1:
						buscarLibroPorTitulo();
						break;
					case 2:
						busquedaAvanzada();
						break;
					case 3:
						listarTodosLosLibros();
						break;
					case 4:
						listarLibrosPorIdioma();
						break;
					case 5:
						listarTodosLosAutores();
						break;
					case 6:
						buscarAutoresVivosEnAno();
						break;
					case 7:
						mostrarEstadisticasIdiomas();
						break;
					case 8:
						System.out.println("Saliendo del sistema...");
						break;
					default:
						System.out.println("Opción no válida. Intente nuevamente.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Debe ingresar un número válido.");
				opcion = 0;
			} catch (Exception e) {
				System.out.println("Error inesperado: " + e.getMessage());
				opcion = 0;
			}
		} while (opcion != 8);
	}

	private void buscarLibroPorTitulo() {
		System.out.print("\nIngrese el título del libro a buscar: ");
		String titulo = scanner.nextLine();

		try {
			var libroDTO = gutendexService.searchBookByTitle(titulo);
			var libroGuardado = bookService.saveBook(libroDTO);

			System.out.println("\n=== LIBRO REGISTRADO ===");
			System.out.println("Título: " + libroGuardado.getTitle());
			System.out.println("Idioma: " + libroGuardado.getLanguage());
			System.out.println("Descargas: " + libroGuardado.getDownloadCount());
			System.out.println("Tipo de medio: " + libroGuardado.getMediaType());
			System.out.println("Copyright: " + (libroGuardado.getCopyright() != null ?
					(libroGuardado.getCopyright() ? "Sí" : "No") : "Desconocido"));

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void busquedaAvanzada() {
		System.out.println("\n=== BÚSQUEDA AVANZADA ===");

		System.out.print("Título (opcional, dejar vacío para omitir): ");
		String titulo = scanner.nextLine();

		System.out.print("Idiomas (opcional, separados por coma, ej: en,es): ");
		String idiomasInput = scanner.nextLine();
		List<String> idiomas = idiomasInput.isEmpty() ? null : List.of(idiomasInput.split(","));

		System.out.print("Año inicio autor (opcional, ej: 1800): ");
		String yearStartInput = scanner.nextLine();
		Integer yearStart = yearStartInput.isEmpty() ? null : Integer.parseInt(yearStartInput);

		System.out.print("Año fin autor (opcional, ej: 1900): ");
		String yearEndInput = scanner.nextLine();
		Integer yearEnd = yearEndInput.isEmpty() ? null : Integer.parseInt(yearEndInput);

		System.out.print("Ordenar por (opcional: popular/ascending/descending): ");
		String orden = scanner.nextLine();
		orden = orden.isEmpty() ? null : orden;

		try {
			BookResponseDTO respuesta = gutendexService.searchBooks(
					titulo.isEmpty() ? null : titulo,
					idiomas,
					yearStart,
					yearEnd,
					orden);

			mostrarResultadosBusqueda(respuesta);

		} catch (Exception e) {
			System.out.println("Error en la búsqueda: " + e.getMessage());
		}
	}

	private void mostrarResultadosBusqueda(BookResponseDTO respuesta) {
		System.out.println("\n=== RESULTADOS DE BÚSQUEDA ===");
		System.out.println("Total de libros encontrados: " + respuesta.getCount());

		if (respuesta.getResults() != null && !respuesta.getResults().isEmpty()) {
			respuesta.getResults().forEach(libro -> {
				System.out.println("\nTítulo: " + libro.getTitle());
				System.out.println("Idiomas: " + String.join(", ", libro.getLanguages()));
				System.out.println("Autores: " + libro.getAuthors().stream()
						.map(a -> a.getName() + " (" + a.getBirth_year() + "-" + a.getDeath_year() + ")")
						.collect(Collectors.joining(", ")));
				System.out.println("Descargas: " + libro.getDownload_count());
				System.out.println("Tipo de medio: " + libro.getMedia_type());
				System.out.println("Copyright: " + (libro.getCopyright() != null ?
						(libro.getCopyright() ? "Sí" : "No") : "Desconocido"));
			});

			if (respuesta.getNext() != null) {
				System.out.println("\nHay más resultados disponibles. ¿Desea ver la siguiente página? (s/n)");
				String continuar = scanner.nextLine();
				if (continuar.equalsIgnoreCase("s")) {
					BookResponseDTO siguientePagina = gutendexService.getBooksByPageUrl(respuesta.getNext());
					mostrarResultadosBusqueda(siguientePagina);
				}
			}
		} else {
			System.out.println("No se encontraron libros con los criterios especificados.");
		}
	}

	private void listarTodosLosLibros() {
		List<BookEntity> libros = bookService.getAllBooks();

		if (libros.isEmpty()) {
			System.out.println("\nNo hay libros registrados aún.");
			return;
		}

		System.out.println("\n=== TODOS LOS LIBROS REGISTRADOS ===");
		libros.forEach(libro -> {
			System.out.println("Título: " + libro.getTitle());
			System.out.println("Idioma: " + libro.getLanguage());
			System.out.println("Descargas: " + libro.getDownloadCount());
			System.out.println("Tipo de medio: " + libro.getMediaType());
			System.out.println("----------------------");
		});
	}

	private void listarLibrosPorIdioma() {
		List<String> idiomas = bookService.getAllLanguages();

		if (idiomas.isEmpty()) {
			System.out.println("\nNo hay libros registrados aún.");
			return;
		}

		System.out.println("\nIdiomas disponibles:");
		idiomas.forEach(System.out::println);

		System.out.print("\nIngrese el idioma a filtrar: ");
		String idioma = scanner.nextLine();

		List<BookEntity> libros = bookService.getBooksByLanguage(idioma);

		if (libros.isEmpty()) {
			System.out.println("\nNo hay libros en ese idioma.");
			return;
		}

		System.out.println("\n=== LIBROS EN " + idioma.toUpperCase() + " ===");
		libros.forEach(libro -> {
			System.out.println("Título: " + libro.getTitle());
			System.out.println("Descargas: " + libro.getDownloadCount());
			System.out.println("----------------------");
		});
	}

	private void listarTodosLosAutores() {
		List<AuthorEntity> autores = bookService.getAllAuthors();

		if (autores.isEmpty()) {
			System.out.println("\nNo hay autores registrados aún.");
			return;
		}

		System.out.println("\n=== TODOS LOS AUTORES REGISTRADOS ===");
		autores.forEach(autor -> {
			System.out.println("Nombre: " + autor.getName());
			System.out.println("Año nacimiento: " + autor.getBirthYear());
			System.out.println("Año fallecimiento: " +
					(autor.getDeathYear() != null ? autor.getDeathYear() : "Desconocido"));
			System.out.println("Libro: " + autor.getBook().getTitle());
			System.out.println("----------------------");
		});
	}

	private void buscarAutoresVivosEnAno() {
		System.out.print("\nIngrese el año a consultar: ");
		try {
			int year = Integer.parseInt(scanner.nextLine());

			if (year <= 0) {
				throw new IllegalArgumentException("El año debe ser un número positivo");
			}

			List<AuthorEntity> autores = bookService.getAuthorsAliveInYear(year);

			if (autores.isEmpty()) {
				System.out.println("\nNo hay autores registrados que estuvieran vivos en " + year);
				return;
			}

			System.out.println("\n=== AUTORES VIVOS EN " + year + " ===");
			autores.forEach(autor -> {
				System.out.println("Nombre: " + autor.getName());
				System.out.println("Años de vida: " + autor.getBirthYear() +
						(autor.getDeathYear() != null ? " - " + autor.getDeathYear() : " - Presente"));
				System.out.println("Libro: " + autor.getBook().getTitle());
				System.out.println("----------------------");
			});

		} catch (NumberFormatException e) {
			System.out.println("Error: Debe ingresar un año válido.");
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void mostrarEstadisticasIdiomas() {
		System.out.println("\n=== ESTADÍSTICAS POR IDIOMA ===");
		System.out.println(bookService.getFormattedLanguageStatistics());
	}
}